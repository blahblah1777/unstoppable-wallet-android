package io.horizontalsystems.bankwallet.modules.swap.approve

import androidx.lifecycle.LiveData
import androidx.lifecycle.LiveDataReactiveStreams
import androidx.lifecycle.ViewModel
import io.horizontalsystems.bankwallet.core.App
import io.horizontalsystems.bankwallet.modules.guides.DataState
import io.reactivex.BackpressureStrategy
import io.reactivex.disposables.CompositeDisposable

class SwapApproveViewModel(private val service: ISwapApproveService) : ViewModel() {

    private val disposables = CompositeDisposable()

    val feeCoinValue: LiveData<String>

    init {
        val feeObservable = service.feeCoinValue.share()

        feeCoinValue = LiveDataReactiveStreams.fromPublisher(feeObservable
                .filter {
                    it is DataState.Success
                }
                .map {
                    it as DataState.Success

                    App.numberFormatter.formatCoin(it.data.value, it.data.coin.code, 0, it.data.coin.decimal)
                }
                .toFlowable(BackpressureStrategy.BUFFER)
        )

    }

    fun onApprove() {
//        service.approve()
    }

}
