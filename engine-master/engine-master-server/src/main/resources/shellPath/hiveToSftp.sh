#!/bin/bash
#------------------------参数说明----------------------------
#--接收
#   remotePaths       - sftp远程服务器文件路径 path1/path2/
#   serverIP          - sftp ip 地址
#   sftpUser          - sftp账号
#   sftpPass          - sftp密码
#   sftpPort          - sftp端口
#   encode_status     - encode_status=0 字符集转化GBK转UTF8
#   exportInfo        - filename|database.tablename|1或filename|database.tablename|pt='20200602'|1 第一列：文件名 第二列：数据库信息 第三列：分区信息 第四列：是否zip压缩
#--变量
#   SYSDATE           -系统日期
#-----------------------------------------------------------


# 接收参数
remotepath=${sftpPath}
serverIP=${serverIP}
sftpUser=${sftpUser}
sftpPass=${sftpPass}
sftpPort=${sftpPort}
exportInfo="${exportInfo}"
encode_status=${encode_status}
skip=${skip}

numA=0
numB=1


# 定义变量
zip_status=0
hive_url=${jdbcUrl}
hive_user=${jdbcUserName}
hive_password=${jdbcPassword}
localPath="/data/gtpBak/Upload/${jobId}"
SYSDATE=`date +%s%N`
respath=~/res.out
LOGPATH=/tmp/
SYSTIME=`date +%Y-%m-%d#%H:%M:%S`
filepath=$localPath/$SYSDATE

if [ ! -d $localPath ]
then
 mkdir -p ${localPath}
fi

# [函数]脚本运行日志
wLog(){
    echo "${SYSTIME}  $1"
    echo "${SYSTIME}  $1" >> ${LOGPATH}/Huaxia3File.log 2>&1
}

return_failure(){
   wLog $1
   exit 1
}

# [函数]处理日期文件夹
createForlder()
{
    cd $1
    if [[ ! -d ${SYSDATE} ]]; then
        mkdir -p ${SYSDATE}/{utf8,gbk,localdir,zipdir,deldir}
        chmod -R 755 ${SYSDATE}
    fi
    cd ${SYSDATE}
}

#[函数]删除第一行
batch_files(){
  if [ -f $1 ]
  then
    sed -i '1d' $1
  fi
}

#[函数]文件编码UTF-8转GBK
encode(){
  filepath_utf8=$1
  yz=`ls $1`
  if [ $? -ne 0 ]
  then
    echo "encode $yz file not found"
    exit
  fi
  for file in `ls $1`
  do
    echo $file
    cd $filepath_utf8
    iconv -f UTF-8 -c -t GBK ${file} > ${file}.GBK
  done
  mv -f $1/*.GBK ${filepath_utf8}/../gbk/
}



#[函数]文件编码GBK转UTF-8
encode_utf(){
  yz=`ls $1`
  if [ $? -ne 0 ]
  then
    echo "encode $1 file not found"
    exit 1
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
    echo "encode $1 file not found"
    exit 1
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

#[函数]批量删除后缀
del_gbk(){
  filepath_gbk=$1
  cd $filepath_gbk
  rename . '' *.GBK

}


# [函数]SFTP非交互式操作
sftp_upload()
{
    expect > $respath 2>&1 <<- EOF
    set timeout 5
    spawn sftp -P $1 $2@$3
    expect {
        "(yes/no)?" {send "yes\r"; exp_continue}
        "*assword:" {send "$4\r"}
    }
    set timeout -1
    expect "sftp>"
    send "lcd $5\r"
    expect "sftp>"
    send "mkdir -p $6\r"
    expect "sftp>"
    send "mput *.csv $6/\r"
    expect "sftp>"
    send "bye\r"
EOF
}

# [函数]SFTP文件重命名
sftp_rename()
{
    expect > $respath 2>&1 <<- EOF
    set timeout 5
    spawn sftp -P $1 $2@$3
    expect {
        "(yes/no)?" {send "yes\r"; exp_continue}
        "*assword:" {send "$4\r"}
    }
    set timeout -1
    expect "sftp>"
    send "cd $5\r"
    expect "sftp>"
    send "rename $6.csv $6\r"
    expect "sftp>"
    send "bye\r"
EOF
}


scp_upload(){

    expect > $respath 2>&1 <<- EOF
    set timeout -1
    spawn bash -c "ssh -p $1  $2@$3 mkdir -p $6"
    expect {
        "(yes/no)?" {send "yes\r"; exp_continue}
        "*assword:" {send "$4\r"}
    }
    expect eof
EOF

    expect > $respath 2>&1 <<- EOF
    set timeout -1
    spawn bash -c "scp -P $1 $5* $2@$3:$6"
    expect {
        "(yes/no)?" {send "yes\r"; exp_continue}
        "*assword:" {send "$4\r"}
    }
    expect eof
EOF


}
#[函数]字符串切割
split_b()
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
  if [ $num -eq 4 ]
  then
    echo $string_a
    array_filename[$i]=`echo $string_a|awk -F '|' '{print $1}'`
    array_tablename[$i]=`echo $string_a|awk -F '|' '{print $2}'`
    array_partition[$i]=`echo $string_a|awk -F '|' '{print $3}'`
    array_zip[$i]=`echo $string_a|awk -F '|' '{print $4}'`
  elif [ $num -eq 3 ]
  then
    array_filename[$i]=`echo $string_a|awk -F '|' '{print $1}'`
    array_tablename[$i]=`echo $string_a|awk -F '|' '{print $2}'`
    array_zip[$i]=`echo $string_a|awk -F '|' '{print $3}'`
  fi
  i=$(($i+1))
done
}


#[函数] hdfs导出hive数据
hdfs_export(){
  which hdfs >/dev/null 2>&1
  if [ $? -ne 0 ]
  then
    echo 'hdfs command not found'
    exit 1
  fi

  db=$1
  database=`echo $1|awk -F '.' '{print $1}'`
  hive_table=`echo $1|awk -F '.' '{print $2}'`
  outFile=$2
  direct_base_dir=/dtInsight/hive/warehouse/${database}.db
  #0: 区分default下和其他db下的情况
  if [ ${database} == 'default' ]; then
       direct_base_dir=/dtInsight/hive/warehouse
  fi

  if [ $# -eq 2 ]
  then
    hdfs dfs -getmerge ${direct_base_dir}/${hive_table}   ${filepath}/${outFile}
  elif [ $# -eq 3 ]
  then
    dtpartition=$3
    hdfs dfs -getmerge ${direct_base_dir}/${hive_table}/${dtpartition}   ${filepath}/${outFile}
  fi
  if [ $? -ne 0 ]
  then
    wLog "hive export fail: $2 $1"
    exit 1
  fi

  if [ $skip -eq 0 ]
  then
    batch_files ${filepath}/${outFile}
  fi

}

zip_init(){
  path_type=$1
  zipname=$2
  if [ $path_type -eq 0 ]
  then
    cd ${filepath}/gbk/
  elif [ $path_type -eq 1 ]
  then
    cd ${filepath}/utf8/
  else
    cd ${filepath}/localdir/
  fi
  which zip >/dev/null 2>&1
  if [ $? -ne 0 ]
  then
    echo "zip command not installed "
    wLog "zip command not installed "
    exit 1
  fi
  zip -r ${zipname}.zip ./
  mv ${zipname}.zip  ${filepath}/zipdir/

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

wLog "huaxia5.sh runing"
wLog "params01 $remotepath"
wLog "params02 $serverIP"
wLog "params03 $sftpUser"
wLog "params04 $sftpPass"
wLog "params05 $sftpPort"
wLog "params06 $exportInfo"
wLog "params07 $encode_status"
wLog "params08 $skip"

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

if [ -d "${localPath}/${SYSDATE}" ]
then
  rm -rf /data/gtpBak/Upload/${jobId}/${SYSDATE}
fi

#创建目录
createForlder ${localPath}
wLog "create forlder ${localPath}/$SYSDATE"

#字符串切割
split_b "$exportInfo"

#批量导出文件
ekey=0
for tb_key in ${array_tablename[@]}
do
  wLog "tablename ${tb_key}"
  if [ ${array_partition[$ekey]-NotDefine} == 'NotDefine' ]
  then
      hdfs_export "${tb_key}" "${array_filename[$ekey]}"
  else
      hdfs_export "${tb_key}" "${array_filename[$ekey]}" "${array_partition[$ekey]}"
  fi


  if [ $encode_status -eq 1 ]
  then
       wLog "############ encode_gbk ${filepath}/${array_filename[$ekey]} ##########"
       encode_gbk ${filepath}/${array_filename[$ekey]}
       [ ${array_zip[$ekey]}  -eq 1 ] && zip_init 0 ${array_filename[$ekey]}
  elif [ $encode_status -eq 0 ]
  then
       wLog "########## encode_utf ${filepath}/${array_filename[$ekey]} ##########"
       encode_utf ${filepath}/${array_filename[$ekey]}
       [ ${array_zip[$ekey]}  -eq 1 ] &&  zip_init 1 ${array_filename[$ekey]}
  else
       wLog "########## local  model ########"
       \cp -f ${filepath}/${array_filename[$ekey]} ${filepath}/${array_filename[$ekey]}.local
       \mv -f ${filepath}/*.local  ${filepath}/localdir/
       mv ${filepath}/${array_filename[$ekey]} ${filepath}/deldir/
       [ ${array_zip[$ekey]}  -eq 1 ] &&  zip_init 2 ${array_filename[$ekey]}
  fi

  if [ ${array_zip[$ekey]}  -eq 0 ]
  then
    if [ $encode_status -eq 1 ]
    then
      rs_localPath=${filepath}/gbk/
    elif [ $encode_status -eq 0 ]
    then
      rs_localPath=${filepath}/utf8/
    else
      rs_localPath=${filepath}/localdir/
    fi
  else
     rs_localPath=${filepath}/zipdir/
  fi

  #批量删除后缀

  cd ${rs_localPath}
  for file in `ls`
  do
    wLog "############rename $file ${file%.*}############"
    mv $file ${file%.*}
  done

  #上传文件
  wLog "############scp_upload "${sftpPort}" "${sftpUser}" "${serverIP}" "${sftpPass}" "${rs_localPath}" "${remotepath}"############"
  scp_upload "${sftpPort}" "${sftpUser}" "${serverIP}" "${sftpPass}" "${rs_localPath}" "${remotepath}"
  mv ${rs_localPath}/* ${filepath}/deldir/
  ekey=$((ekey+1))
done




#for fl_key in ${array_filename[@]}
#do
#  sftp_rename "${sftpPort}" "${sftpUser}" "${serverIP}" "${sftpPass}" "${remotepath}" "${fl_key}"
#  if [ $? -eq 0 ]
#  then
#    wLog "SFTP rename $fl_key file"
#    exit 1
#  fi
#done

wLog "##########rm -rf /data/gtpBak/Upload/${jobId}/${SYSDATE}#########"
rm -rf /data/gtpBak/Upload/${jobId}/${SYSDATE}

wLog "END "
