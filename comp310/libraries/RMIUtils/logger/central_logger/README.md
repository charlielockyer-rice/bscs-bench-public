# Centralized Logging Service and Test Client Demos

This package contains two stand-alone, fully-functional apps:
1. `server.controller.CentralLoggerApp` -- A app that provides remote logging services accessible via an IRemoteLogService stub bound to the local RMI Registry using the IRemoteLogService.DEFAULT_BOUND_NAME bound name.
    * **To Run**:  Run the "CentralLoggerApp.launch" file.
2. `client.controller.RemoteLoggingServiceTestClient` -- A test client that is capable of connecting to a remote IRemoteLogService such as that provided by the CentralLoggerApp.
    * **To Run**:  Run the "RemoteLoggingServiceTestClient.launch" file.

 