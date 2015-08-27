/*******************************************************************************
 * Copyright 2012 Egor Moseevskiy.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/

package su.whs.call.helper.task;

import android.os.AsyncTask;

import su.whs.call.helper.Delay;
import su.whs.call.helper.task.listeners.Async;
import su.whs.call.helper.task.listeners.AsyncProvider;
import su.whs.call.helper.task.listeners.AsyncResultProvider;
import su.whs.call.helper.task.listeners.DelayListener;


public class Task extends AsyncTask<Void, Void, Object> {

    private AsyncProvider asyncProvider = null;
    private Async async = null;
    private AsyncResultProvider asyncResultProvider = null;
    private DelayListener delayListener = null;
    private int delay = 0;

    private Task(AsyncProvider asyncProvider, Async async, AsyncResultProvider asyncResultProvider, DelayListener delayListener, int delay) {
        this.asyncProvider = asyncProvider;
        this.async = async;
        this.delayListener = delayListener;
        this.asyncResultProvider = asyncResultProvider;
        this.delay = delay;
        execute();
    }

    public static Task async(AsyncProvider asyncProvider) {
        return new Task(asyncProvider, null, null, null, 0);
    }

    public static Task async(Async async) {
        return new Task(null, async, null, null, 0);
    }

    public static Task async(AsyncResultProvider asyncResultProvider) {
        return new Task(null, null, asyncResultProvider, null, 0);
    }

    public static Task delay(int delay, DelayListener delayListener) {
        return new Task(null, null, null, delayListener, delay);
    }

    public static Task delayedAsync(int delay, Async async) {
        return new Task(null, async, null, null, delay);
    }

    @Override
    protected Object doInBackground(Void... voids) {
        if (delay > 0)
            Delay.thread(delay);
        if (asyncProvider != null)
            asyncProvider.asyncWork();
        if (async != null)
            async.asyncWork();
        if (asyncResultProvider != null)
            return asyncResultProvider.asyncWork();
        return null;
    }

    @Override
    protected void onPostExecute(Object result) {
        if (delayListener != null)
            delayListener.now();
        if (asyncProvider != null)
            asyncProvider.after();
        if (asyncResultProvider != null)
            asyncResultProvider.after(result);
        super.onPostExecute(result);
    }

    public Boolean stop() {
        return cancel(false);
    }

}
