```xml

+---------------------------------------------------+
|                  WorkManager                      |
|  High-level API for guaranteed background jobs    |
|  - Survives app restarts                          |
|  - Handles constraints (Wi-Fi, charging, etc.)    |
|  - Uses best scheduler under the hood             |
+---------------------------------------------------+
                     |
                     v
+---------------------------------------------------+
|                 Schedulers                        |
|  Decide *where* tasks run                         |
|  - Schedulers.io() → I/O-bound work               |
|  - Schedulers.computation() → CPU-heavy work      |
|  - AndroidSchedulers.mainThread() → UI updates    |
+---------------------------------------------------+
                     |
                     v
+---------------------------------------------------+
|                 Thread Pools                      |
|  Efficiently manage multiple threads              |
|  - ExecutorService / ThreadPoolExecutor           |
|  - Reuse threads to avoid overhead                |
|  - Prevents too many threads crashing the app     |
+---------------------------------------------------+
                     |
                     v
+---------------------------------------------------+
|                 Threads                           |
|  Lowest-level unit of execution                   |
|  - Runs actual code instructions                  |
|  - Managed by OS                                  |
+---------------------------------------------------+
```
