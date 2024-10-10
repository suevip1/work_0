#!/bin/sh

# 替换以下变量为所需的值：
# CODE - 要运行的 Python 代码
# PYTHON_VERSION - 要使用的 Python 版本（"2" 或 "3"）
# CMD - Python 解释器的附加命令行参数
# PYTHON2_PATH - Python2 的路径
# PYTHON3_PATH - Python3 的路径
pythonVersion="${PYTHON_VERSION}"
cmd="${CMD}"
job_id="${JOB_ID}"
debug=${DEBUG}
python2_path="${PYTHON2_PATH}"
python3_path="${PYTHON3_PATH}"

# 创建临时 Python 脚本文件
create_temp_python_file() {
  [ "$debug" = true ] && echo "Creating temporary Python script file..."
  temp_python_file=$(mktemp)
  cat << EOF > "$temp_python_file"
${CODE}
EOF
}

# 检查临时 Python 脚本文件是否存在
check_temp_file_exists() {
  [ "$debug" = true ] && echo "Checking if temporary Python script file exists..."
  if [ ! -f "$temp_python_file" ]; then
    return 1
  fi
  return 0
}

# 选择 Python 命令
choose_python_cmd() {
  [ "$debug" = true ] && echo "Choosing Python command..."
  if [ "$pythonVersion" = "2" ]; then
    python_cmd="$python2_path"
  elif [ "$pythonVersion" = "3" ]; then
    python_cmd="$python3_path"
  else
    return 1
  fi
  return 0
}

# 运行临时 Python 脚本并将输出添加前缀
run_temp_python_script() {
  [ "$debug" = true ] && echo "Running temporary Python script..."
  $python_cmd "$temp_python_file" $cmd
  return $?  # 返回运行状态
}

# 清理临时 Python 脚本文件
cleanup_temp_python_file() {
  [ "$debug" = true ] && echo "Cleaning up temporary Python script file..."
  rm "$temp_python_file"
}

# 使用 trap 在脚本退出时执行 cleanup_temp_python_file 函数
trap cleanup_temp_python_file EXIT

# 主函数
main() {
  [ "$debug" = true ] && echo "Starting execution for Job ID: $job_id"
  create_temp_python_file
  if check_temp_file_exists; then
    if choose_python_cmd; then
      run_temp_python_script
      exit_status=$?  # 保存运行状态
      if [ $exit_status -ne 0 ]; then  # 如果运行状态不为 0，表示运行失败
        echo "Error: Failed to run temporary Python script."
        exit $exit_status
      fi
    else
      echo "Error: Unsupported Python version."
      exit 1
    fi
  else
    echo "Error: Failed to create temporary Python script."
    exit 1
  fi
}

main