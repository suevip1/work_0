// package com.dtstack.engine.api.service;
//
// import com.dtstack.sdk.core.common.ApiResponse;
// import com.dtstack.sdk.core.common.DtInsightServer;
// import com.dtstack.sdk.core.feign.Param;
// import com.dtstack.sdk.core.feign.RequestLine;
//
//
// @Deprecated
// public interface DownloadService extends DtInsightServer {
//
//     @RequestLine("POST /node/download/component/downloadFile")
//     ApiResponse<Void> handleDownload(@Param("componentId") Long componentId, @Param("downloadType") Integer downloadType, @Param("componentType") Integer componentType,
//                                @Param("versionName") String versionName, @Param("clusterId") Long clusterId);
// }
