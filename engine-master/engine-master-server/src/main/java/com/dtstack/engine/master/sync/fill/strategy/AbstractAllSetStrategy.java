package com.dtstack.engine.master.sync.fill.strategy;

import com.google.common.collect.Lists;

import java.util.List;

/**
 * @Auther: dazhi
 * @Date: 2022/7/7 10:29 AM
 * @Email:dazhi@dtstack.com
 * @Description:
 */
public abstract class AbstractAllSetStrategy implements AllSetStrategy {

    protected static class DAGNode {
        // 目标节点（R集合里面的节点）
        private String aimNode;
        // 未完加载成的路径
        private List<DAGPath> paths;
        // 已加载完成的路径
        private List<DAGPath> finishPaths;

        public DAGNode() {
            finishPaths = Lists.newArrayList();
        }

        public String getAimNode() {
            return aimNode;
        }

        public void setAimNode(String aimNode) {
            this.aimNode = aimNode;
        }

        public List<DAGPath> getPaths() {
            return paths;
        }

        public void setPaths(List<DAGPath> paths) {
            this.paths = paths;
        }

        public List<DAGPath> getFinishPaths() {
            return finishPaths;
        }

        public void setFinishPaths(List<DAGPath> finishPaths) {
            this.finishPaths = finishPaths;
        }

        @Override
        public String toString() {
            return "DAGNode{" +
                    "aimNode='" + aimNode + '\'' +
                    ", paths=" + paths +
                    ", finishPaths=" + finishPaths +
                    '}';
        }
    }

    protected static class DAGPath {
        // 运行中的路径头
        private String top;
        // 运行中的路径尾
        private String end;
        // R集合在路径中的头
        private String RTop;
        // R集合在路径中的未
        private String REnd;
        // 是否被中断 false 为被中断  true 被中断
        private Boolean interruptMark = Boolean.FALSE;
        // 路径的元素
        private List<String> taskKeys = Lists.newArrayList();
        // R集合的路径元素
        private List<String> RTaskKeys = Lists.newArrayList();

        public DAGPath() {
        }

        public DAGPath(DAGPath dagPath) {
            this.top = dagPath.getTop();
            this.end = dagPath.getEnd();
            this.RTop = dagPath.getRTop();
            this.REnd = dagPath.getREnd();
            this.taskKeys = Lists.newArrayList(dagPath.getTaskKeys());
            this.RTaskKeys = Lists.newArrayList(dagPath.getRTaskKeys());
        }

        public String getTop() {
            return top;
        }

        public void setTop(String top) {
            this.top = top;
        }

        public String getEnd() {
            return end;
        }

        public void setEnd(String end) {
            this.end = end;
        }

        public String getRTop() {
            return RTop;
        }

        public void setRTop(String RTop) {
            this.RTop = RTop;
        }

        public String getREnd() {
            return REnd;
        }

        public void setREnd(String REnd) {
            this.REnd = REnd;
        }

        public List<String> getTaskKeys() {
            return taskKeys;
        }

        public Boolean getInterruptMark() {
            return interruptMark;
        }

        public void setInterruptMark(Boolean interruptMark) {
            this.interruptMark = interruptMark;
        }

        public void setTaskKeys(List<String> taskKeys) {
            this.taskKeys = taskKeys;
        }

        public List<String> getRTaskKeys() {
            return RTaskKeys;
        }

        public void setRTaskKeys(List<String> RTaskKeys) {
            this.RTaskKeys = RTaskKeys;
        }

        public void addChildTaskKey(String childTaskKey) {
            List<String> taskKeys = this.getTaskKeys();
            taskKeys.add(childTaskKey);
        }

        public void addChildRTaskKey(String childTaskKey) {
            addChildTaskKey(childTaskKey);
            RTaskKeys.addAll(taskKeys);
            taskKeys.clear();
        }

        public void addChildRTaskKeys(List<String> rTaskKeys) {
            RTaskKeys.addAll(rTaskKeys);
        }

    }
}
