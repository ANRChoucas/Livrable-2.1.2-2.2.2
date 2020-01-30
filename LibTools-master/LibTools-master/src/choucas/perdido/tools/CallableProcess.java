package choucas.perdido.tools;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class CallableProcess implements Callable<Integer> {

    private Process p;

    public CallableProcess(Process process) {
        p = process;
    }

    public Integer call() throws Exception {
        return p.waitFor();
    }
    
    
    public static void exec(String cmd, int seconds) throws Exception {
		ExecutorService service = Executors.newSingleThreadExecutor();
	    Process process = Runtime.getRuntime().exec(cmd);
	    try {
	        Callable<Integer> call = new CallableProcess(process);
	        Future<Integer> future = service.submit(call);
	        int exitValue = future.get(seconds, TimeUnit.SECONDS);
	        if (exitValue != 0) {
	            //throw new Exception("Process did not exit correctly");
	        }
	    } catch (ExecutionException e) {
	        throw new Exception("Process failed to execute", e);
	    } catch (TimeoutException e) {
	        process.destroy();
	        throw new Exception("Process timed out", e);
	    } finally {
	        service.shutdown();
	    }
    }
}
