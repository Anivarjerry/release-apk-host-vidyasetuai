2026-08-25 20:27:05.948 11799-11799 NtViewRootImpl          com.vidyasetuai                      D  mPopUpViewOffsets: offset=(0.0, 0.0), scale=(1.0, 1.0)
2026-08-25 20:27:42.334 11799-11799 InsetsController        com.vidyasetuai                      D  Setting requestedVisibleTypes to -1 (was -9)
2026-08-25 20:27:47.222 11799-11799 InsetsController        com.vidyasetuai                      D  Setting requestedVisibleTypes to -9 (was -1)
2026-08-25 20:28:14.211 11799-11799 InsetsController        com.vidyasetuai                      D  Setting requestedVisibleTypes to -1 (was -9)
2026-08-25 20:28:16.090 11799-11799 InsetsController        com.vidyasetuai                      D  Setting requestedVisibleTypes to -9 (was -1)
2026-08-25 20:28:18.426 11799-11799 InsetsController        com.vidyasetuai                      D  Setting requestedVisibleTypes to -1 (was -9)
2026-08-25 20:28:21.312 11799-11799 InsetsController        com.vidyasetuai                      D  Setting requestedVisibleTypes to -9 (was -1)
2026-08-25 20:47:30.423 19898-20064 DisplayManager          com.vidyasetuai                      I  Choreographer implicitly registered for the refresh rate.
2026-08-25 20:48:56.861 20432-20465 DisplayManager          com.vidyasetuai                      I  Choreographer implicitly registered for the refresh rate.
2026-08-25 20:53:45.938 20432-20823 Supabase-Realtime       com.vidyasetuai                      I  Heartbeat received
2026-08-25 20:53:46.632 20432-20432 WindowOnBackDispatcher  com.vidyasetuai                      W  sendCancelIfRunning: isInProgress=false callback=androidx.activity.OnBackPressedDispatcher$Api34Impl$createOnBackAnimationCallback$1@4e7a474
2026-08-25 20:53:51.665 20432-20474 Supabase-Realtime       com.vidyasetuai                      E  Error while listening for messages. Trying again in 7s (Fix with AI)
                                                                                                    java.net.SocketException: Software caused connection abort
                                                                                                    	at java.net.SocketInputStream.socketRead0(Native Method)
                                                                                                    	at java.net.SocketInputStream.socketRead(SocketInputStream.java:118)
                                                                                                    	at java.net.SocketInputStream.read(SocketInputStream.java:173)
                                                                                                    	at java.net.SocketInputStream.read(SocketInputStream.java:143)
                                                                                                    	at com.android.org.conscrypt.ConscryptEngineSocket$SSLInputStream.readFromSocket(ConscryptEngineSocket.java:1015)
                                                                                                    	at com.android.org.conscrypt.ConscryptEngineSocket$SSLInputStream.processDataFromSocket(ConscryptEngineSocket.java:979)
                                                                                                    	at com.android.org.conscrypt.ConscryptEngineSocket$SSLInputStream.readUntilDataAvailable(ConscryptEngineSocket.java:894)
                                                                                                    	at com.android.org.conscrypt.ConscryptEngineSocket$SSLInputStream.read(ConscryptEngineSocket.java:867)
                                                                                                    	at okio.InputStreamSource.read(JvmOkio.kt:93)
                                                                                                    	at okio.AsyncTimeout$source$1.read(AsyncTimeout.kt:153)
                                                                                                    	at okio.RealBufferedSource.request(RealBufferedSource.kt:211)
                                                                                                    	at okio.RealBufferedSource.require(RealBufferedSource.kt:204)
                                                                                                    	at okio.RealBufferedSource.readByte(RealBufferedSource.kt:214)
                                                                                                    	at okhttp3.internal.ws.WebSocketReader.readHeader(WebSocketReader.kt:119)
                                                                                                    	at okhttp3.internal.ws.WebSocketReader.processNextFrame(WebSocketReader.kt:102)
                                                                                                    	at okhttp3.internal.ws.RealWebSocket.loopReader(RealWebSocket.kt:293)
                                                                                                    	at okhttp3.internal.ws.RealWebSocket$connect$1.onResponse(RealWebSocket.kt:195)
                                                                                                    	at okhttp3.internal.connection.RealCall$AsyncCall.run(RealCall.kt:519)
                                                                                                    	at java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1100)
                                                                                                    	at java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:624)
                                                                                                    	at java.lang.Thread.run(Thread.java:1572)
2026-08-25 20:53:58.725 20432-20823 Supabase-Realtime       com.vidyasetuai                      E  Error while trying to connect to realtime websocket. Trying again in 7s
                                                                                                    URL: wss://elffhfoefptjgodlckec.supabase.co/realtime/v1/websocket?apikey=eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImVsZmZoZm9lZnB0amdvZGxja2VjIiwicm9sZSI6ImFub24iLCJpYXQiOjE3ODAyNDEzODYsImV4cCI6MjA5NTgxNzM4Nn0.C7krhyzt4OLl0nxbXxSNwe2mnAEr1YufL_cLgU9OS40&vsn=1.0.0
2026-08-25 20:54:04.078 20432-20823 Supabase-Core           com.vidyasetuai                      E  GET request to endpoint /rest/v1/user_sessions failed with exception Unable to resolve host "elffhfoefptjgodlckec.supabase.co": No address associated with hostname
2026-08-25 20:54:04.129 20432-20964 Supabase-Core           com.vidyasetuai                      E  PATCH request to endpoint /rest/v1/user_sessions failed with exception Unable to resolve host "elffhfoefptjgodlckec.supabase.co": No address associated with hostname
2026-08-25 20:54:04.130 20432-20964 VidyaSetu_AuthManager   com.vidyasetuai                      E  Failed to sync FCM token with server: HTTP request to https://elffhfoefptjgodlckec.supabase.co/rest/v1/user_sessions?user_id=eq.269bcbf8-7229-419a-86a2-8f6c3e7864d3 (PATCH) failed with message: Unable to resolve host "elffhfoefptjgodlckec.supabase.co": No address associated with hostname
2026-08-25 20:54:05.774 20432-20474 Supabase-Realtime       com.vidyasetuai                      E  Error while trying to connect to realtime websocket. Trying again in 7s
                                                                                                    URL: wss://elffhfoefptjgodlckec.supabase.co/realtime/v1/websocket?apikey=eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImVsZmZoZm9lZnB0amdvZGxja2VjIiwicm9sZSI6ImFub24iLCJpYXQiOjE3ODAyNDEzODYsImV4cCI6MjA5NTgxNzM4Nn0.C7krhyzt4OLl0nxbXxSNwe2mnAEr1YufL_cLgU9OS40&vsn=1.0.0
2026-08-25 20:54:12.843 20432-20581 Supabase-Realtime       com.vidyasetuai                      E  Error while trying to connect to realtime websocket. Trying again in 7s
                                                                                                    URL: wss://elffhfoefptjgodlckec.supabase.co/realtime/v1/websocket?apikey=eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImVsZmZoZm9lZnB0amdvZGxja2VjIiwicm9sZSI6ImFub24iLCJpYXQiOjE3ODAyNDEzODYsImV4cCI6MjA5NTgxNzM4Nn0.C7krhyzt4OLl0nxbXxSNwe2mnAEr1YufL_cLgU9OS40&vsn=1.0.0
2026-08-25 20:54:16.703 20432-20432 ActivityThread          com.vidyasetuai                      D  handleResumeActivity#3 r.window=com.android.internal.policy.PhoneWindow@eef8e7ca.mFinished = falsewillBeVisible = true
2026-08-25 20:54:16.713 20432-20432 BufferQueueConsumer     com.vidyasetuai                      D  [](id:4fd000000001,api:0,p:-1,c:20432) connect: controlledByApp=false
2026-08-25 20:54:16.713 20432-20432 BLASTBufferQueue        com.vidyasetuai                      D  [VRI[MainActivity]#1](f:0,a:0) constructor()
2026-08-25 20:54:16.713 20432-20432 BLASTBufferQueue        com.vidyasetuai                      D  [VRI[MainActivity]#1](f:0,a:0) update width=1084 height=2412 format=-1 mTransformHint=0
2026-08-25 20:54:16.731 20432-20465 BLASTBufferQueue        com.vidyasetuai                      D  [VRI[MainActivity]#1](f:0,a:1) acquireNextBufferLocked size=1084x2412 mFrameNumber=1 applyTransaction=true mTimestamp=479195522256715(auto) mPendingTransactions.size=0 graphicBufferId=87754771791893 transform=0
2026-08-25 20:54:16.782 20432-20432 NtViewRootImpl          com.vidyasetuai                      D  mPopUpViewOffsets: offset=(0.0, 0.0), scale=(1.0, 1.0)
2026-08-25 20:54:16.783 20432-20432 InsetsController        com.vidyasetuai                      D  hide(ime(), fromIme=false)
2026-08-25 20:54:16.783 20432-20432 ImeTracker              com.vidyasetuai                      I  com.vidyasetuai:29bfc7a8: onCancelled at PHASE_CLIENT_ALREADY_HIDDEN
2026-08-25 20:54:20.661 20432-20581 Supabase-Realtime       com.vidyasetuai                      I  Connected to realtime websocket!
2026-08-25 20:54:26.897 20432-20964 CampusRepo              com.vidyasetuai                      W  Realtime insert flow stopped: StandaloneCoroutine was cancelled
2026-08-25 20:54:26.901 20432-20622 CampusRepo              com.vidyasetuai                      W  Realtime update flow stopped: StandaloneCoroutine was cancelled
2026-08-25 20:54:26.901 20432-20473 CampusRepo              com.vidyasetuai                      W  Realtime typing broadcast flow stopped: StandaloneCoroutine was cancelled
2026-08-25 20:54:26.910 20432-20581 CampusRepo              com.vidyasetuai                      D  Realtime chat channel cleanly unsubscribed and removed.
2026-08-25 20:54:27.702 20432-20474 CampusRepo              com.vidyasetuai                      D  Realtime chat channel cleanly unsubscribed and removed.
2026-08-25 20:54:28.223 20432-20672 PowerHalMgrImpl         com.vidyasetuai                      I  hdl:519386, pid:20432 
2026-08-25 20:54:28.228 20432-20672 PowerHalMgrImpl         com.vidyasetuai                      I  hdl:519386, pid:20432 
2026-08-25 20:54:28.257 20432-20432 ScrollIdentify          com.vidyasetuai                      I  on fling
2026-08-25 20:54:28.263 20432-20672 PowerHalMgrImpl         com.vidyasetuai                      I  hdl:519389, pid:20432 
2026-08-25 20:54:28.520 20432-20474 Supabase-Realtime       com.vidyasetuai                      I  Connected to realtime websocket!
2026-08-25 20:54:28.524 20432-20474 CampusRepo              com.vidyasetuai                      D  Realtime channel subscribed for conversation: conv_21513c76-f57b-45bd-9714-9a944abedd0e_269bcbf8-7229-419a-86a2-8f6c3e7864d3
2026-08-25 20:54:30.949 20432-20432 InsetsController        com.vidyasetuai                      D  show(ime(), fromIme=false)
2026-08-25 20:54:30.950 20432-20432 ImeTracker              com.vidyasetuai                      I  com.vidyasetuai:e0e98fa1: onRequestShow at ORIGIN_CLIENT reason SHOW_SOFT_INPUT_BY_INSETS_API fromUser false
2026-08-25 20:54:30.950 20432-20432 InsetsController        com.vidyasetuai                      D  Setting requestedVisibleTypes to -1 (was -9)
2026-08-25 20:54:30.969 20432-20432 ImeTracker              com.vidyasetuai                      I  com.vidyasetuai:af329b4d: onRequestShow at ORIGIN_CLIENT reason SHOW_SOFT_INPUT fromUser false
2026-08-25 20:54:30.969 20432-20432 InsetsController        com.vidyasetuai                      D  show(ime(), fromIme=false)
2026-08-25 20:54:30.969 20432-20432 ImeTracker              com.vidyasetuai                      I  com.vidyasetuai:af329b4d: onCancelled at PHASE_CLIENT_REPORT_REQUESTED_VISIBLE_TYPES
2026-08-25 20:54:31.272 20432-20672 PowerHalMgrImpl         com.vidyasetuai                      I  hdl:519394, pid:20432 
2026-08-25 20:54:31.372 20432-20432 ImeTracker              com.vidyasetuai                      I  com.vidyasetuai:e0e98fa1: onShown
2026-08-25 20:54:32.929 20432-20432 InsetsController        com.vidyasetuai                      D  Setting requestedVisibleTypes to -9 (was -1)
2026-08-25 20:54:32.930 20432-20432 WindowOnBackDispatcher  com.vidyasetuai                      W  sendCancelIfRunning: isInProgress=false callback=android.view.ImeBackAnimationController@af11bc4
2026-08-25 20:54:32.939 20432-20432 ImeTracker              com.vidyasetuai                      I  com.vidyasetuai:9accfa59: onRequestHide at ORIGIN_CLIENT reason HIDE_SOFT_INPUT_REQUEST_HIDE_WITH_CONTROL fromUser true
2026-08-25 20:54:32.941 20432-20432 InsetsController        com.vidyasetuai                      D  hide(ime(), fromIme=false)
2026-08-25 20:54:32.941 20432-20432 ImeTracker              com.vidyasetuai                      I  com.vidyasetuai:9accfa59: onCancelled at PHASE_CLIENT_ALREADY_HIDDEN
2026-08-25 20:54:33.091 20432-20623 CampusRemote            com.vidyasetuai                      D  SendMessage RPC response: {"status": "SENT", "success": true, "created_at": "2026-08-25T15:24:33.719317+00:00", "expires_at": "2026-08-26T15:24:33.719317+00:00", "message_id": "db854175-291a-4e34-8609-4113bca57bb6", "conversation_id": "conv_21513c76-f57b-45bd-9714-9a944abedd0e_269bcbf8-7229-419a-86a2-8f6c3e7864d3"}
2026-08-25 20:54:33.297 20432-20432 ImeTracker              com.vidyasetuai                      I  system_server:cf668b90: onCancelled at PHASE_CLIENT_ON_CONTROLS_CHANGED
2026-08-25 20:54:37.031 20432-20622 CampusRepo              com.vidyasetuai                      W  Realtime insert flow stopped: StandaloneCoroutine was cancelled
2026-08-25 20:54:37.035 20432-20474 CampusRepo              com.vidyasetuai                      W  Realtime update flow stopped: StandaloneCoroutine was cancelled
2026-08-25 20:54:37.035 20432-20623 CampusRepo              com.vidyasetuai                      D  Realtime chat channel cleanly unsubscribed and removed.
2026-08-25 20:54:37.038 20432-20823 CampusRepo              com.vidyasetuai                      W  Realtime typing broadcast flow stopped: StandaloneCoroutine was cancelled
2026-08-25 20:54:37.413 20432-20432 InsetsController        com.vidyasetuai                      D  hide(ime(), fromIme=false)
2026-08-25 20:54:37.414 20432-20432 ImeTracker              com.vidyasetuai                      I  com.vidyasetuai:e7a0c3a8: onRequestHide at ORIGIN_CLIENT reason HIDE_SOFT_INPUT_BY_INSETS_API fromUser false
2026-08-25 20:54:37.415 20432-20432 ImeTracker              com.vidyasetuai                      I  com.vidyasetuai:e7a0c3a8: onCancelled at PHASE_CLIENT_ALREADY_HIDDEN
2026-08-25 20:54:37.518 20432-20437 com.vidyasetuai         com.vidyasetuai                      I  Compiler allocated 4140KB to compile void com.vidyasetuai.feature_campus.presentation.screen.CampusHomeScreenKt.CampusHomeScreen(com.vidyasetuai.feature_campus.presentation.viewmodel.CampusViewModel, boolean, androidx.compose.ui.Modifier, androidx.compose.runtime.Composer, int, int)
