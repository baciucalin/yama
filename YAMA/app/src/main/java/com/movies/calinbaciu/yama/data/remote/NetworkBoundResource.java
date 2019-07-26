package com.movies.calinbaciu.yama.data.remote;

import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.WorkerThread;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.Observer;

import com.movies.calinbaciu.yama.data.Resource;

import helpers.ExecutorHelper;

/**
 * A generic class that can provide a resource backed by both the sqlite database and the network.
 * @param <ResultType>
 * @param <RequestType>
 */

public abstract class NetworkBoundResource<ResultType, RequestType> {

    private ExecutorHelper mExecutors;
    private final MediatorLiveData<Resource<ResultType>> result = new MediatorLiveData<>();

    @MainThread
    public NetworkBoundResource(ExecutorHelper executorHelper) {
        mExecutors = executorHelper;
        // Send loading state to UI
        result.setValue(Resource.<ResultType>loading(null));

        final LiveData<ResultType> dbSource = loadFromDb();

        result.addSource(dbSource, new Observer<ResultType>() {
            @Override
            public void onChanged(ResultType data) {
                result.removeSource(dbSource);
                if (shouldFetch(data)) {
                    fetchFromNetwork(dbSource);
                } else {
                    result.addSource(dbSource, new Observer<ResultType>() {
                        @Override
                        public void onChanged(ResultType newData) {
                            setValue(Resource.success(newData));
                        }
                    });
                }
            }
        });
    }

    @MainThread
    private void setValue(Resource<ResultType> newValue) {
        if (result.getValue() != newValue) {
            result.setValue(newValue);
        }
    }

    /**
     * Fetch the data from network, persist into DB and then
     * send it back to UI.
     */
    private void fetchFromNetwork(final LiveData<ResultType> dbSource) {
        final LiveData<ApiResponseWrapper<RequestType>> apiResponse = createCall();
        // we re-attach dbSource as a new source, it will dispatch its latest value quickly
        result.addSource(dbSource, new Observer<ResultType>() {
            @Override
            public void onChanged(ResultType newData) {
                setValue(Resource.loading(newData));
            }
        });
        result.addSource(apiResponse, new Observer<ApiResponseWrapper<RequestType>>() {
            @Override
            public void onChanged(final ApiResponseWrapper<RequestType> response) {
                result.removeSource(apiResponse);
                result.removeSource(dbSource);
                if (response.isSuccessful()) {
                    mExecutors.diskIO().execute(new Runnable() {
                        @Override
                        public void run() {
                            saveCallResult(response.body);
                            mExecutors.mainThread().execute(new Runnable() {
                                @Override
                                public void run() { // this requests a new live data/gets the last cached data
                                    result.addSource(loadFromDb(), new Observer<ResultType>() {
                                        @Override
                                        public void onChanged(ResultType newData) {
                                            setValue(Resource.success(newData));
                                        }
                                    });
                                }
                            });
                        }
                    });
                } else {
                    onFetchFailed();
                    result.addSource(dbSource, new Observer<ResultType>() {
                        @Override
                        public void onChanged(ResultType newData) {
                            setValue(Resource.error(response.getError().getMessage(), newData));
                        }
                    });
                }
            }
        });
    }

    // Called to save the result of the API response into the database.
    @WorkerThread
    protected abstract void saveCallResult(@NonNull RequestType item);

    // Called with the data in the database to decide whether to fetch
    // potentially updated data from the network.
    @MainThread
    protected abstract boolean shouldFetch(@Nullable ResultType data);

    // Called to get the cached data from the database.
    @NonNull
    @MainThread
    protected abstract LiveData<ResultType> loadFromDb();

    // Called to create the API call.
    @NonNull
    @MainThread
    protected abstract LiveData<ApiResponseWrapper<RequestType>> createCall();

    // Called when the fetch fails. The child class may want to reset components
    // like rate limiter.
    @NonNull
    @MainThread
    protected abstract void onFetchFailed();

    // Returns a LiveData object that represents the resource that's implemented
    // in the base class.
    public final LiveData<Resource<ResultType>> getAsLiveData() {
        return result;
    }
}
