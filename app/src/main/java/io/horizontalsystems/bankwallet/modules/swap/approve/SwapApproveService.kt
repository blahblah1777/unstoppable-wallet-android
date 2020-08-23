package io.horizontalsystems.bankwallet.modules.swap.approve

import io.horizontalsystems.bankwallet.core.FeeRatePriority
import io.horizontalsystems.bankwallet.core.IFeeRateProvider
import io.horizontalsystems.bankwallet.core.adapters.Erc20Adapter
import io.horizontalsystems.bankwallet.entities.Coin
import io.horizontalsystems.bankwallet.entities.CoinValue
import io.horizontalsystems.bankwallet.modules.guides.DataState
import io.horizontalsystems.core.entities.Currency
import io.horizontalsystems.ethereumkit.models.TransactionWithInternal
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject
import java.math.BigDecimal

class SwapApproveService(
        private val amount: BigDecimal,
        private val erc20Adapter: Erc20Adapter,
        private val feeRateProvider: IFeeRateProvider,
        private val baseCurrency: Currency,
        private val feeCoin: Coin,
        private val spenderAddress: String
) : ISwapApproveService {

    private var gasPrice: Long = 0
    private var gasLimit: Long = 0

    override val feeCoinValue = BehaviorSubject.create<DataState<CoinValue>>()
    override val approvalState = BehaviorSubject.create<DataState<TransactionWithInternal>>()

    private val disposables = CompositeDisposable()

    init {
        feeCoinValue.onNext(DataState.Loading())

        feeRateProvider.feeRates()
                .flatMap {
                    val feeRateInfo = it.first { it.priority == FeeRatePriority.HIGH }

                    gasPrice = feeRateInfo.feeRate

                    erc20Adapter.estimateApprove(spenderAddress, amount, gasPrice)
                }
                .subscribeOn(Schedulers.io())
                .subscribe({
                    gasLimit = it

                    val feeCoinValue = CoinValue(feeCoin, erc20Adapter.fee(gasPrice, gasLimit))

                    this.feeCoinValue.onNext(DataState.Success(feeCoinValue))
                }, {
                    feeCoinValue.onNext(DataState.Error(it))
                })
                .let {
                    disposables.add(it)
                }

    }

    fun approve() {
//        approvalState.onNext(DataState.Loading())
//
//
//
//        erc20Adapter.es(erc20Adapter.receiveAddress, amount)
//
//        erc20Adapter.approve(erc20Adapter.receiveAddress, amount, feeRateInfo.feeRate)
//                .subscribe({
//                    approvalState.onNext(DataState.Success(it))
//                }, {
//                    approvalState.onNext(DataState.Error(it))
//                })
//                .let {
//                    disposables.add(it)
//                }
    }

}
