update alert_rule
set template = '[${sign!}]离线任务：${task.name!}失败告警
任务：${task.name!}
项目：${project.projectAlias!}
租户：${tenant.tenantName!}
调度类型：${jobScheduleType!}
计划时间：${jobCycTime!}
开始时间：${job.execStartTime!,dateFormat=''yyyy-MM-dd HH:mm:ss''}
结束时间：${job.execEndTime!,dateFormat=''yyyy-MM-dd HH:mm:ss''}
运行时长：${jobExecTime!}
当前状态：${statusStr!}
<%
       if(errorMsg!=null){
            println(''失败原因:'' + errorMsg);
        }
%>
责任人：${user.userName!}
请及时处理!'
where `key` in ('error_status');