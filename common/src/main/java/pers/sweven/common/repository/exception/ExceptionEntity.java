package pers.sweven.common.repository.exception;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ExceptionEntity {

    /**
     * message : 请登录
     * exception : Symfony\Component\HttpKernel\Exception\HttpException
     * file : /www/wwwroot/app/releases/44/vendor/laravel/framework/src/Illuminate/Foundation/Application.php
     * line : 1067
     * trace : [{"file":"/www/wwwroot/app/releases/44/vendor/laravel/framework/src/Illuminate/Foundation/helpers.php","line":44,"function":"abort","class":"Illuminate\\Foundation\\Application","type":"->"},{"file":"/www/wwwroot/app/releases/44/app/Http/Middleware/Authenticate.php","line":17,"function":"abort"},{"file":"/www/wwwroot/app/releases/44/vendor/laravel/framework/src/Illuminate/Auth/Middleware/Authenticate.php","line":83,"function":"redirectTo","class":"App\\Http\\Middleware\\Authenticate","type":"->"},{"file":"/www/wwwroot/app/releases/44/app/Http/Middleware/Authenticate.php","line":46,"function":"unauthenticated","class":"Illuminate\\Auth\\Middleware\\Authenticate","type":"->"},{"file":"/www/wwwroot/app/releases/44/vendor/laravel/framework/src/Illuminate/Auth/Middleware/Authenticate.php","line":42,"function":"authenticate","class":"App\\Http\\Middleware\\Authenticate","type":"->"},{"file":"/www/wwwroot/app/releases/44/vendor/laravel/framework/src/Illuminate/Pipeline/Pipeline.php","line":167,"function":"handle","class":"Illuminate\\Auth\\Middleware\\Authenticate","type":"->"},{"file":"/www/wwwroot/app/releases/44/vendor/laravel/framework/src/Illuminate/View/Middleware/ShareErrorsFromSession.php","line":49,"function":"Illuminate\\Pipeline\\{closure}","class":"Illuminate\\Pipeline\\Pipeline","type":"->"},{"file":"/www/wwwroot/app/releases/44/vendor/laravel/framework/src/Illuminate/Pipeline/Pipeline.php","line":167,"function":"handle","class":"Illuminate\\View\\Middleware\\ShareErrorsFromSession","type":"->"},{"file":"/www/wwwroot/app/releases/44/vendor/laravel/framework/src/Illuminate/Session/Middleware/StartSession.php","line":116,"function":"Illuminate\\Pipeline\\{closure}","class":"Illuminate\\Pipeline\\Pipeline","type":"->"},{"file":"/www/wwwroot/app/releases/44/vendor/laravel/framework/src/Illuminate/Session/Middleware/StartSession.php","line":62,"function":"handleStatefulRequest","class":"Illuminate\\Session\\Middleware\\StartSession","type":"->"},{"file":"/www/wwwroot/app/releases/44/vendor/laravel/framework/src/Illuminate/Pipeline/Pipeline.php","line":167,"function":"handle","class":"Illuminate\\Session\\Middleware\\StartSession","type":"->"},{"file":"/www/wwwroot/app/releases/44/vendor/laravel/framework/src/Illuminate/Cookie/Middleware/AddQueuedCookiesToResponse.php","line":37,"function":"Illuminate\\Pipeline\\{closure}","class":"Illuminate\\Pipeline\\Pipeline","type":"->"},{"file":"/www/wwwroot/app/releases/44/vendor/laravel/framework/src/Illuminate/Pipeline/Pipeline.php","line":167,"function":"handle","class":"Illuminate\\Cookie\\Middleware\\AddQueuedCookiesToResponse","type":"->"},{"file":"/www/wwwroot/app/releases/44/vendor/laravel/framework/src/Illuminate/Cookie/Middleware/EncryptCookies.php","line":67,"function":"Illuminate\\Pipeline\\{closure}","class":"Illuminate\\Pipeline\\Pipeline","type":"->"},{"file":"/www/wwwroot/app/releases/44/vendor/laravel/framework/src/Illuminate/Pipeline/Pipeline.php","line":167,"function":"handle","class":"Illuminate\\Cookie\\Middleware\\EncryptCookies","type":"->"},{"file":"/www/wwwroot/app/releases/44/vendor/laravel/framework/src/Illuminate/Pipeline/Pipeline.php","line":103,"function":"Illuminate\\Pipeline\\{closure}","class":"Illuminate\\Pipeline\\Pipeline","type":"->"},{"file":"/www/wwwroot/app/releases/44/vendor/laravel/framework/src/Illuminate/Routing/Router.php","line":687,"function":"then","class":"Illuminate\\Pipeline\\Pipeline","type":"->"},{"file":"/www/wwwroot/app/releases/44/vendor/laravel/framework/src/Illuminate/Routing/Router.php","line":662,"function":"runRouteWithinStack","class":"Illuminate\\Routing\\Router","type":"->"},{"file":"/www/wwwroot/app/releases/44/vendor/laravel/framework/src/Illuminate/Routing/Router.php","line":628,"function":"runRoute","class":"Illuminate\\Routing\\Router","type":"->"},{"file":"/www/wwwroot/app/releases/44/vendor/laravel/framework/src/Illuminate/Routing/Router.php","line":617,"function":"dispatchToRoute","class":"Illuminate\\Routing\\Router","type":"->"},{"file":"/www/wwwroot/app/releases/44/vendor/laravel/framework/src/Illuminate/Foundation/Http/Kernel.php","line":165,"function":"dispatch","class":"Illuminate\\Routing\\Router","type":"->"},{"file":"/www/wwwroot/app/releases/44/vendor/laravel/framework/src/Illuminate/Pipeline/Pipeline.php","line":128,"function":"Illuminate\\Foundation\\Http\\{closure}","class":"Illuminate\\Foundation\\Http\\Kernel","type":"->"},{"file":"/www/wwwroot/app/releases/44/vendor/barryvdh/laravel-debugbar/src/Middleware/InjectDebugbar.php","line":67,"function":"Illuminate\\Pipeline\\{closure}","class":"Illuminate\\Pipeline\\Pipeline","type":"->"},{"file":"/www/wwwroot/app/releases/44/vendor/laravel/framework/src/Illuminate/Pipeline/Pipeline.php","line":167,"function":"handle","class":"Barryvdh\\Debugbar\\Middleware\\InjectDebugbar","type":"->"},{"file":"/www/wwwroot/app/releases/44/vendor/laravel/framework/src/Illuminate/Foundation/Http/Middleware/TransformsRequest.php","line":21,"function":"Illuminate\\Pipeline\\{closure}","class":"Illuminate\\Pipeline\\Pipeline","type":"->"},{"file":"/www/wwwroot/app/releases/44/vendor/laravel/framework/src/Illuminate/Pipeline/Pipeline.php","line":167,"function":"handle","class":"Illuminate\\Foundation\\Http\\Middleware\\TransformsRequest","type":"->"},{"file":"/www/wwwroot/app/releases/44/vendor/laravel/framework/src/Illuminate/Foundation/Http/Middleware/TransformsRequest.php","line":21,"function":"Illuminate\\Pipeline\\{closure}","class":"Illuminate\\Pipeline\\Pipeline","type":"->"},{"file":"/www/wwwroot/app/releases/44/vendor/laravel/framework/src/Illuminate/Pipeline/Pipeline.php","line":167,"function":"handle","class":"Illuminate\\Foundation\\Http\\Middleware\\TransformsRequest","type":"->"},{"file":"/www/wwwroot/app/releases/44/vendor/laravel/framework/src/Illuminate/Foundation/Http/Middleware/ValidatePostSize.php","line":27,"function":"Illuminate\\Pipeline\\{closure}","class":"Illuminate\\Pipeline\\Pipeline","type":"->"},{"file":"/www/wwwroot/app/releases/44/vendor/laravel/framework/src/Illuminate/Pipeline/Pipeline.php","line":167,"function":"handle","class":"Illuminate\\Foundation\\Http\\Middleware\\ValidatePostSize","type":"->"},{"file":"/www/wwwroot/app/releases/44/vendor/laravel/framework/src/Illuminate/Foundation/Http/Middleware/CheckForMaintenanceMode.php","line":63,"function":"Illuminate\\Pipeline\\{closure}","class":"Illuminate\\Pipeline\\Pipeline","type":"->"},{"file":"/www/wwwroot/app/releases/44/vendor/laravel/framework/src/Illuminate/Pipeline/Pipeline.php","line":167,"function":"handle","class":"Illuminate\\Foundation\\Http\\Middleware\\CheckForMaintenanceMode","type":"->"},{"file":"/www/wwwroot/app/releases/44/vendor/fruitcake/laravel-cors/src/HandleCors.php","line":37,"function":"Illuminate\\Pipeline\\{closure}","class":"Illuminate\\Pipeline\\Pipeline","type":"->"},{"file":"/www/wwwroot/app/releases/44/vendor/laravel/framework/src/Illuminate/Pipeline/Pipeline.php","line":167,"function":"handle","class":"Fruitcake\\Cors\\HandleCors","type":"->"},{"file":"/www/wwwroot/app/releases/44/vendor/fideloper/proxy/src/TrustProxies.php","line":57,"function":"Illuminate\\Pipeline\\{closure}","class":"Illuminate\\Pipeline\\Pipeline","type":"->"},{"file":"/www/wwwroot/app/releases/44/vendor/laravel/framework/src/Illuminate/Pipeline/Pipeline.php","line":167,"function":"handle","class":"Fideloper\\Proxy\\TrustProxies","type":"->"},{"file":"/www/wwwroot/app/releases/44/vendor/laravel/framework/src/Illuminate/Pipeline/Pipeline.php","line":103,"function":"Illuminate\\Pipeline\\{closure}","class":"Illuminate\\Pipeline\\Pipeline","type":"->"},{"file":"/www/wwwroot/app/releases/44/vendor/laravel/framework/src/Illuminate/Foundation/Http/Kernel.php","line":140,"function":"then","class":"Illuminate\\Pipeline\\Pipeline","type":"->"},{"file":"/www/wwwroot/app/releases/44/vendor/laravel/framework/src/Illuminate/Foundation/Http/Kernel.php","line":109,"function":"sendRequestThroughRouter","class":"Illuminate\\Foundation\\Http\\Kernel","type":"->"},{"file":"/www/wwwroot/app/releases/44/public/index.php","line":55,"function":"handle","class":"Illuminate\\Foundation\\Http\\Kernel","type":"->"}]
     */

    private String message;
    private String exception;
    private String file;
    private int line;
    private List<Trace> trace;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getException() {
        return exception;
    }

    public void setException(String exception) {
        this.exception = exception;
    }

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }

    public int getLine() {
        return line;
    }

    public void setLine(int line) {
        this.line = line;
    }

    public List<Trace> getTrace() {
        return trace;
    }

    public void setTrace(List<Trace> trace) {
        this.trace = trace;
    }

    public static class Trace {
        /**
         * file : /www/wwwroot/app/releases/44/vendor/laravel/framework/src/Illuminate/Foundation/helpers.php
         * line : 44
         * function : abort
         * class : Illuminate\Foundation\Application
         * type : ->
         */

        private String file;
        private int line;
        private String function;
        @SerializedName("class")
        private String classX;
        private String type;

        public String getFile() {
            return file;
        }

        public void setFile(String file) {
            this.file = file;
        }

        public int getLine() {
            return line;
        }

        public void setLine(int line) {
            this.line = line;
        }

        public String getFunction() {
            return function;
        }

        public void setFunction(String function) {
            this.function = function;
        }

        public String getClassX() {
            return classX;
        }

        public void setClassX(String classX) {
            this.classX = classX;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }
    }
}