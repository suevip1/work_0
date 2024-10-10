#!/bin/sh

# 替换以下变量为所需的值：
# DOWNLOAD_URL - 下载 URL
# TARGET_SCRIPT - 目标脚本文件名
# DT_TOKEN - 认证 Token
# RETRY_COUNT - 重试次数
# CONNECT_TIMEOUT - 连接超时时间
# MAX_TIME - 最大执行时间
# PARAMS - 请求参数
# JOB_ID - 工作 ID
# PYTHON_VERSION - Python 版本（python2 或 python3）
# PYTHON2_PATH - Python2 的路径
# PYTHON3_PATH - Python3 的路径
# CMD - 附加命令行参数
url="${DOWNLOAD_URL}"
target_script="${TARGET_SCRIPT}"
TOKEN="${DT_TOKEN}"
header="Cookie: dt_token=$TOKEN"
retry_count="${RETRY_COUNT}"
connect_timeout="${CONNECT_TIMEOUT}"
max_time="${MAX_TIME}"
params="${PARAMS}"
job_id="${JOB_ID}"
pythonVersion="${PYTHON_VERSION}"
cmd="${CMD}"
debug=${DEBUG}
tryTelnetOrPing="${TRY_TELNET_OR_PING}"

python2_path="${PYTHON2_PATH}"
python3_path="${PYTHON3_PATH}"

download_dir="agentJobArchive/$job_id"

# 获取当前时间戳
timestamp=$(date +%s)

# 创建一个包含 job_id 和时间戳的文件名
temp_file="${job_id}_${timestamp}.zip"

# 清理临时文件的函数
cleanup_temp_files() {
  [ "$debug" = true ] && echo "Cleaning up temporary script file..."
  rm -rf "$download_dir/*"
}

# 使用 trap 在脚本退出时执行 cleanup_temp_files 函数
trap cleanup_temp_files EXIT

# 创建下载目录的函数
create_download_directory() {
  [ "$debug" = true ] && echo "Creating download directory (if it doesn't exist)..."
  mkdir -p "$download_dir"
  return $?
}

# 检查 telnet 命令是否存在
check_telnet() {
  command -v telnet >/dev/null 2>&1
}


# 获取URL的主机名和端口号
get_host_port() {
  host=$(echo $1 | awk -F/ '{print $3}' | awk -F: '{print $1}')
  port=$(echo $1 | awk -F/ '{print $3}' | awk -F: '{print $2}')
  [ -z "$port" ] && port=80
  echo $host $port
}

function eztimeout() { perl -e 'alarm shift; exec @ARGV' "$@"; }

# 使用 telnet 或 ping 测试连通性
function test_connectivity() {
  host=$1
  port=$2
  if check_telnet; then
    FILENAME="/tmp/__port_check_$(uuidgen)"
    RESULT=$(eztimeout 1 telnet $host $port  &> $FILENAME; cat $FILENAME | tail -n1 |awk '{print $1$2}')
    rm -f $FILENAME
    echo "$RESULT"
    if [ "$RESULT" == "Escapecharacter" ]; then
      return 0
    else
      return 1
    fi
  else
    ping -c 1 $host >/dev/null 2>&1
    return $?
  fi
}

# 下载文件的函数
download_file() {
   [ "$debug" = true ] && echo "Downloading job file..."
   [ "$debug" = true ] && echo ${url}

   host_port=$(get_host_port $url)
   host=$(echo $host_port | awk '{print $1}')
   port=$(echo $host_port | awk '{print $2}')

   # 测试主机的连通性, 不同机器表现出来的结果无法预知，默认关闭该配置。http://zenpms.dtstack.cn/zentao/bug-view-87340.html
   if [ "$tryTelnetOrPing" == "1" ]; then
      test_connectivity $host $port
      if [ $? -ne 0 ]; then
        echo "Failed to connect to the host $host on port $port. Exiting..."
        return 1
      fi
   fi

   curl --retry $retry_count --connect-timeout $connect_timeout \
        -H "$header" -o "$download_dir/$temp_file" "${url}?${params}"
   return $?
}

# 解压缩文件的函数
extract_zip() {
  [ "$debug" = true ] && echo "Extracting downloaded ZIP file..."
  unzip -tq "$download_dir/$temp_file"
  if [ $? -eq 0 ]; then
    unzip -o "$download_dir/$temp_file" -d "$download_dir"
    return 0
  else
    [ "$debug" = true ] && echo "The downloaded ZIP file is corrupted. Exiting..."
    return 1
  fi
}

execute_target_script() {
  [ "$debug" = true ] && echo "Checking file type and executing..."

  # Add loop to change permissions for all .sh files
  for file in "$download_dir"/*.sh; do
    [ -f "$file" ] && chmod +x "$file"
  done

  if [ -f "$download_dir/$target_script" ]; then
      case "${target_script##*.}" in
          py)
              if [ "$pythonVersion" = "2" ]; then
                  [ "$debug" = true ] && echo "Executing script with Python2 and passing additional arguments..."
                  $python2_path "$download_dir/$target_script" $cmd || exit 1
              elif [ "$pythonVersion" = "3" ]; then
                  [ "$debug" = true ] && echo "Executing script with Python3 and passing additional arguments..."
                  $python3_path "$download_dir/$target_script" $cmd || exit 1
              else
                  echo "Unknown Python version: $pythonVersion"
                  exit 1
              fi
              ;;
          sh)
              [ "$debug" = true ] && echo "Executing Shell script..."
              cd "$download_dir" && ./"$target_script" || exit 1
              ;;
          *)
              echo "Unsupported file type: ${target_script##*.}"
              exit 1
              ;;
      esac
  else
      echo "Specified file not found: $target_script"
      exit 1
  fi
  return 0
}


main() {
  [ "$debug" = true ] && echo "Starting execution for Job ID: $job_id"
  create_download_directory
  if [ $? -eq 0 ]; then
    download_file
    if [ $? -eq 0 ]; then
      extract_zip
      if [ $? -eq 0 ]; then
        execute_target_script
        if [ $? -eq 0 ]; then
          exit 0
        else
          [ "$debug" = true ] && echo "Error: Failed to execute the target script."
          exit 1
        fi
      else
        [ "$debug" = true ] && echo "Error: Failed to extract the ZIP file."
        exit 1
      fi
    else
      [ "$debug" = true ] && echo "Error: Failed to download the file."
      exit 1
    fi
  else
    [ "$debug" = true ] && echo "Error: Failed to create the download directory."
    exit 1
  fi
}

main
