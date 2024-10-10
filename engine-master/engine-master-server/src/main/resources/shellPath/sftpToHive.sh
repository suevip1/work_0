#!/bin/bash

#   SKIP              - 判断是否去除表头 skip=0 去除表头
#   remotePaths       - 下载文件信息  /data/shier/sftp/breast_c.csv|shier.tb_filecopy_1|1
#   增加分区信息 /data/shier/sftp/breast_c.csv|shier.tb_filecopy_1|pt='20200611'|1 第一列：文件路径 第二列： 表信息 第三列：分区信息 第四列：判断转码 （0:关闭覆盖模式，1：开启覆盖模式）
#   serverIP          - sftp ip 地址
#   sftpUser          - sftp账号
#   sftpPass          - sftp密码
#   sftpPort          - sftp端口
#   encode_status     - encode_status=0 字符集转化GBK转UTF8  encode_status=1 字符集转化UTF8转GBK
#   checkfile_status. - checkfile_status=0 开启检查文件是否存在
#   --变量
#   SYSDATE           -系统日期
#-----------------------------------------------------------

# 接收参数
skip=${skip}
remotePaths="${remotePath}"
serverIP=${serverIP}
sftpUser=${sftpUser}
sftpPass=${sftpPass}
sftpPort=${sftpPort}
encode_status=${encode_status}
checkfile_status=${checkfile_status}
numA=0
numB=1




# 定义变量
localPath="/data/gtpBak/Download/${jobId}"
SYSDATE=`date +%s%N`
respath=/data/gtpBak/Download/${jobId}/${SYSDATE}/res.out
LOGPATH=/tmp/
SYSTIME=`date +%Y-%m-%d#%H:%M:%S`

# [函数]脚本运行日志
wLog(){
    LOGTIME=`date +%Y-%m-%d-%H:%M:%S`
    echo "${LOGTIME}  $1"
    echo "${LOGTIME}  $1" >> ${LOGPATH}/Huaxia3File.log 2>&1
}

check_hivejdbc(){
 beeline ${jdbcUserName}  -u "${jdbcUrl}"  ${jdbcPassword}  -e "show databases;"

 if [ $? -eq 1 ];then

 echo "hive jdbc connect faild,please check service of  hiveserver2"
 exit 1
 fi
}

result_staus(){
  if [ $2 -ne 0 ]
  then
    wLog "$1 status $2"
    exit 1
  fi
}

# [函数]处理日期文件夹
createForlder()
{
    mkdir -p ${localPath}
    cd $1
    if [[ ! -d ${SYSDATE} ]]; then
        mkdir -p ${SYSDATE}/{utf8,gbk,localdir,zipdir,deldir}
        chmod -R 755 ${SYSDATE}
    fi
    cd ${SYSDATE}
   hdfs dfs -mkdir -p hdfs://ns1/dtInsight/${localPath}
}

#【函数】检查命令是否存在
command_check(){
  which $1 > /dev/null 2>&1
  if [ $? -ne 0 ]
  then
    wLog '$1 command not found'
    exit 1
  fi

}

#[函数]检查是否是zip文件
zip_check(){
   unzip  -q -t $1 > /dev/null 2>&1
   if [ $? -ne 0 ]
   then
     echo "$1 is not zip file"
     wLog "$1 is not zip file"
     exit 1
   fi

}
#[函数]文件编码GBK转UTF-8
encode_utf(){
  yz=`ls $1`
  if [ $? -ne 0 ]
  then
    echo "encode $1 file not found"
    exit
  fi
  for file in `ls $1`
  do
    echo $file
    #mv -f ${file} ${file}.GBK
    iconv -f GBK -c -t UTF-8 ${file} > ${file}.UTF8
    if [ $? -eq 0 ]
    then
      mv ${file} ${localPath}/${SYSDATE}/deldir/
    fi
  done
  filepath=${yz%/*}
  mv -f ${filepath}/*.UTF8 ${localPath}/${SYSDATE}/utf8/

}
#[函数]文件编码UTF-8转GBK
encode_gbk(){
  yz=`ls $1`
  if [ $? -ne 0 ]
  then
    echo "encode $yz file not found"
    exit
  fi
  for file in `ls $1`
  do
    echo $file
    iconv -f UTF-8 -c -t GBK ${file} > ${file}.GBK
    if [ $? -eq 0 ]
    then
      mv ${file} ${localPath}/${SYSDATE}/deldir/
    fi
  done
  filepath=${yz%/*}
  mv -f $filepath/*.GBK ${localPath}/${SYSDATE}/gbk/
}


#[函数]检查远程端文件信息
get_filelist()
{
expect > $respath 2>&1 << EOF
set timeout 5
spawn sftp -P $1 $2@$3
expect {
    "(yes/no)?" {send "yes\r"; exp_continue}
    "*assword:" {send "$4\r"}
}
expect "sftp>"
send "ls $5\r"
expect "sftp>"
send "bye\r"
EOF
}


#[函数]删除第一行
batch_files(){
  if [ -f $1 ]
  then
    sed -i '1d' $1
  fi
}

#[函数]字符串切割
split_a()
{
string_files="$1"
i=0
echo $string_files
array=(${string_files//,/ })
for string_a in ${array[@]}
do
  echo $string_a
  array2=(${string_a//|/ })
  num=${#array2[@]}
  if [ $num -eq 5 ]
  then
    array_filepath[$i]=`echo $string_a|awk -F '|' '{print $1}'`
    array_hive[$i]=`echo $string_a|awk -F '|' '{print $2}'`
    partitions[$i]=`echo $string_a|awk -F '|' '{print $3}'`
    model[$i]=`echo $string_a|awk -F '|' '{print $4}'`
    zip_status[$i]=`echo $string_a|awk -F '|' '{print $5}'`
  elif [ $num -eq 4 ]
  then
    array_filepath[$i]=`echo $string_a|awk -F '|' '{print $1}'`
    array_hive[$i]=`echo $string_a|awk -F '|' '{print $2}'`
    model[$i]=`echo $string_a|awk -F '|' '{print $3}'`
    zip_status[$i]=`echo $string_a|awk -F '|' '{print $4}'`
    yz=`echo $string_a|awk -F '|' '{print $3}'|grep 'pt'`
    if [ $? -eq 0 ]
    then
      echo "error: params number is $num, please check remotePaths params: $string_files "
      exit 1
    fi
  else
    echo "error: please check remotePaths params: $string_files "
    wLog "error: please check remotePaths params: $string_files "
    exit 1
  fi
  #array_partition[$i]=`echo $string_a|awk -F '#' '{print $2}'`
  i=$(($i+1))
done
}

partition_split(){
    partition_info2=$1
    partition_info=
    if [[ $partition_info2 =~ '/' ]];then
      array=(${partition_info2//// })
      for partition in ${array[@]}
      do
        info=`echo $partition|awk -F '=' '{print $1}'`=\"`echo $partition|awk -F '=' '{print $2}'`\"
        [ ! $partition_info  ]&& partition_info=$info||partition_info=$partition_info,$info
      done
    else
        info=`echo $partition_info2|awk -F '=' '{print $1}'`=\"`echo $partition_info2|awk -F '=' '{print $2}'`\"
        partition_info=$info
    fi


}


#[函数] HIVE导入
from_hdfs_import(){

  if [ ! -f "$2" ]
  then
   echo "import $2 file not found"
   wLog "import $2 file not found"
   exit 1
  fi

  if [ $# -eq 4 ]
  then
      wLog "hive_import $1 $2 $3 $4"
      partition_split $3
      beeline ${jdbcUserName}  -u "${jdbcUrl}"  ${jdbcPassword}  -e "load data  inpath 'hdfs://ns1/dtInsight/$2' into table $1 partition($partition_info);"
      result_staus "hive_import" $?
  fi

  if [ $# -eq 3 ]
  then
      wLog "hive_import $1 $2 $3"
      beeline ${jdbcUserName}  -u "${jdbcUrl}"  ${jdbcPassword}  -e "load data inpath 'hdfs://ns1/dtInsight/$2' into table $1;"
      result_staus "hive_import" $?
  fi
}


#[函数]覆盖模式
hive_overwrite(){
  which hdfs >/dev/null 2>&1
  if [ $? -ne 0 ]
  then
    echo 'hdfs command not found'
    exit 1
  fi
  database=`echo $1|awk -F '.' '{print $1}'`
  hive_table=`echo $1|awk -F '.' '{print $2}'`
  dtpartition=$2
  direct_base_dir=/dtInsight/hive/warehouse/${database}.db
  #0: 区分default下和其他db下的情况
  if [ ${database} == 'default' ]; then
       direct_base_dir=/dtInsight/hive/warehouse
  fi
 echo 'direct_base_dir：${direct_base_dir} ----- ${database} -- ${hive_table}'
  #0:带分区操作 1:不带分区
  if [ $3 -eq 0 ]
  then
    dtpartition=$2
    backup_dir=/dtInsight/hive/warehouse.bak/${database}.db/${hive_table}/${dtpartition}_${SYSDATE}
    direct_dir=${direct_base_dir}/${hive_table}/${dtpartition}
  elif [ $3 -eq 1 ]
  then
    backup_dir=/dtInsight/hive/warehouse.bak/${database}.db/${hive_table}_${SYSDATE}
    direct_dir=${direct_base_dir}/${hive_table}
  fi
  hdfs dfs -mkdir -p ${backup_dir}
  hdfs dfs -mv ${direct_dir}/*  ${backup_dir}/ > $respath 2>&1
  grep "No such file" $respath > /dev/null 2>&1
  [ $? -eq 0 ]&& hdfs dfs -mkdir -p $direct_dir

}



#[函数] zip文件初始操作
zip_init(){
  zipname=${1##*/}
  key=$2
  if [ -f "${localPath}/${SYSDATE}/zipdir/${zipname}.${key}" ]
  then
    mkdir zipdir/${key}
    which unzip > /dev/null 2>&1
    if [ $? -ne 0 ]
    then
      echo 'unzip command not found'
      exit 1
    fi

    unzip ${localPath}/${SYSDATE}/zipdir/${zipname}.${key}  -d zipdir/${key}
    for file in `ls zipdir/${key}`
    do
      file=`echo $file|sed 's/://g'`
      if [ -d "$file" ]
      then
        continue
      fi
      mv zipdir/${key}/${file}  ${localPath}/${SYSDATE}/zipdir/${key}/$file.$key
      #批量操作去除表头
        if [ $skip -eq 0 ]
        then
          batch_files ${localPath}/${SYSDATE}/zipdir/${key}/$file.$key
        fi

        if [ $encode_status -eq 0 ]
        then
           encode_utf ${localPath}/${SYSDATE}/zipdir/${key}/$file.$key
           wLog "zip ${file}.${dkey}   encode UTF8 success"
        elif [ $encode_status -eq 1 ]
        then
           encode_gbk ${localPath}/${SYSDATE}/zipdir/${key}/$file.$key
           wLog "zip ${file}.${dkey}   encode GBK success"
        else
          filepath=${file%/*}
          \mv -f ${localPath}/${SYSDATE}/zipdir/${key}/$file.$key  ${localPath}/${SYSDATE}/zipdir/${key}/${file}.${key}.local
          \mv -f ${localPath}/${SYSDATE}/zipdir/${key}/*.local ${localPath}/${SYSDATE}/localdir/
        fi
    done
  fi
}

#[函数]下载文件初始处理
download_init(){
    #下载文件标识
    checkfile=$1
    dkey=$2
    filename=${checkfile##*/}
    yz=`ls ${localPath}/${SYSDATE}/${filename}`
    if [ $? -ne 0 ]
    then
      if [ $checkfile_status -eq 0 ]
      then
        echo '$checkfile not file'
        exit 1
      else
        continue
      fi
    fi

    for file in `ls ${localPath}/${SYSDATE}/${filename}`
    do
        file=`echo $file|sed 's/://g'`
        if [ -d "$file" ]
        then
           continue
        fi

        if [ ! -f "$file" ]
        then
          continue
        fi

      if [ ${zip_status[$dkey]} -eq 1 ]
      then
        command_check "unzip"
        zip_check ${file}
        mv $file  ${file}.${dkey}
        mv ${file}.${dkey} ${localPath}/${SYSDATE}/zipdir/
        zip_init ${file} ${dkey}
      else
        if [ ${file##*.} = zip ]
        then
          mv $file  ${file}.${dkey}
          mv ${file}.${dkey} ${localPath}/${SYSDATE}/zipdir/
          zip_init ${file} ${dkey}
        fi
        mv $file  ${file}.${dkey}

        #批量操作去除表头
        if [ $skip -eq 0 ]
        then
          batch_files ${file}.${dkey}
        fi

        if [ $encode_status -eq 0 ]
        then
           encode_utf ${file}.${dkey}
           echo "${file}.${dkey}   encode UTF8 success"
           wLog "${file}.${dkey}   encode UTF8 success"
        elif [ $encode_status -eq 1 ]
        then
           encode_gbk ${file}.${dkey}
           echo "${file}.${dkey}   encode GBK success"
           wLog "${file}.${dkey}   encode GBK success"
        else
          filepath=${file%/*}
          \cp -f ${file}.${dkey} ${file}.${dkey}.local
          \mv -f ${filepath}/*.local ${localPath}/${SYSDATE}/localdir/
          \mv -f ${file}.${dkey}  ${localPath}/${SYSDATE}/deldir/
        fi
      fi
    done
}

# [函数]SFTP检查连通性
sftp_check_connect()
{
    expect > $respath 2>&1 <<- EOF
    set timeout 15
    spawn sftp -P $3 $2@$1
    expect {
        "(yes/no)?" {send "yes\r"; exp_continue}
        "password:" {send "$4\r"}
        "sftp>" {
                send "quit\r"
                exit 0
                }
            }

       expect {
            "sftp>" {
                # 连接成功
                send "quit\r"
                exit 0
            }
            "Permission denied" {
                # 密码错误
                exit 1
            }
            timeout {
                # 超时
                exit 2
            }
        }
EOF
}

# [函数]SFTP非交互式操作
sftp_download()
{
    expect > $respath 2>&1 <<- EOF
    set timeout 15
    spawn sftp -P $1 $2@$3
    expect {
        "(yes/no)?" {send "yes\r"; exp_continue}
        "*assword:" {send "$4\r"}
    }
    expect {
      "sftp>" {
           send "mget $5\r"
      }
      "Permission denied" {
          # 密码错误
          exit 1
      }
      timeout {
          # 超时
          exit 2
      }
    }
    expect "sftp>"
    send "bye\r"
EOF
}

wLog "huaxia2.sh runing"
wLog "params01 $skip"
wLog "params02 $remotePaths"
wLog "params03 $serverIP"
wLog "params04 $sftpUser"
wLog "params05 $sftpPass"
wLog "params06 $sftpPort"


check_hivejdbc

if [ -d "${localPath}/${SYSDATE}" ]
then
rm -rf /data/gtpBak/Download/${jobId}/${SYSDATE}
fi

#创建目录
createForlder ${localPath}
wLog "create forlder ${localPath}/$SYSDATE"

#字符串切割
split_a "$remotePaths"

sftp_check_connect $serverIP $sftpUser $sftpPort $sftpPass

var=$?
if (($var == $numA))
then
echo "sftp连接成功"
elif  (($var == $numB))
then
echo  "sftp密码错误"
exit 1
else
  echo "sftp连接超时了"
  exit 1
fi

#下载文件
dkey=0
for checkfile in ${array_filepath[@]}
do
  wLog "sftp_download ${checkfile}"
  sftp_download "${sftpPort}" "${sftpUser}" "${serverIP}" "${sftpPass}" "${checkfile}"
  if [ $? -ne 0 ]; then
       wLog "SFTP Download error exit 1"
       exit 1
  fi
  grep "not found" $respath
  if [ $? -eq 0 ]
  then
    wLog "SFTP Download $checkfile not found"
    if [ $checkfile_status -eq 0 ]
    then
      echo '$checkfile not found'
      exit 1
    fi
    continue
  fi
  download_init $checkfile $dkey
  dkey=$((dkey+1))
done

hdfs dfs -put  ${localPath}/${SYSDATE} hdfs://ns1/dtInsight/data/gtpBak/Download/${jobId}

if [ $? -eq 0 ]; then
    wLog "${localPath}/${SYSDATE} put success to /dtInsight/data/gtpBak/Download/${jobId}"
else
    wLog "${localPath}/${SYSDATE} put error to /dtInsight/data/gtpBak/Download/${jobId} exit the program "
    exit 1
fi

rs_localPath=${localPath}/${SYSDATE}/
if [ $encode_status -eq 0 ]
then
rs_localPath=${localPath}/${SYSDATE}/utf8
elif [ $encode_status -eq 1 ]
then
rs_localPath=${localPath}/${SYSDATE}/gbk
else
rs_localPath=${localPath}/${SYSDATE}/localdir
fi

#导入hive
tb_key=0
for table in ${array_hive[@]}
do
  echo "import table $table"
  wLog "import table $table"

  #覆盖模式，清空表内容
  if [ ${model[$tb_key]} -eq 1 ]
  then
    if [ ${partitions-NotDefine} == 'NotDefine' ]
    then
      echo "partitions is null"
      hive_overwrite ${table} 'isnull' 1
    else
      hive_overwrite ${table} ${partitions[$tb_key]} 0
    fi
  fi

  for file in `ls ${rs_localPath}/*.$tb_key.*`
  do
    echo "run hive_import ${table} ${file}"
    wLog "run hive_import ${table} ${file}"
    if [ ${partitions-NotDefine} == 'NotDefine' ]
    then
    from_hdfs_import "${table}" "${file}" "${model[$tb_key]}"
    else
    from_hdfs_import "${table}" "${file}" "${partitions[$tb_key]}"  "${model[$tb_key]}"
    fi
    if [ $? -ne 0 ]
    then
      wLog "$SYSDATE  $tableInfo hive load faild"
      exit 1
    fi
  wLog "import $file success"
  done
  tb_key=$((tb_key+1))
done
rm -rf /data/gtpBak/Download/${jobId}/${SYSDATE}
hdfs dfs -rmr hdfs://ns1/dtInsight/data/gtpBak/Download/${jobId}/${SYSDATE}
wLog "rm $file"
wLog "END "