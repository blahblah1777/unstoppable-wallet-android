package io.horizontalsystems.bankwallet.modules.swap.approve

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import io.horizontalsystems.bankwallet.R
import io.horizontalsystems.bankwallet.core.App
import io.horizontalsystems.bankwallet.core.setOnSingleClickListener
import io.horizontalsystems.bankwallet.entities.Coin
import io.horizontalsystems.bankwallet.ui.extensions.BaseBottomSheetDialogFragment
import kotlinx.android.synthetic.main.fragment_swap_approve.*
import java.math.BigDecimal

class SwapApproveFragment : BaseBottomSheetDialogFragment() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setContentView(R.layout.fragment_swap_approve)

        val coin = requireArguments().getParcelable<Coin>("coin")!!
        val amount = requireArguments().getSerializable("amount") as BigDecimal
        val spenderAddress = requireArguments().getString("spenderAddress")!!

        val viewModel by viewModels<SwapApproveViewModel> {
            SwapApproveModule.Factory(coin, amount, spenderAddress)
        }

        setTitle(getString(R.string.Approve_Title))
        setSubtitle(getString(R.string.Swap_Title))
        setHeaderIcon(R.drawable.ic_swap)

        coinAmount.text = App.numberFormatter.formatCoin(amount, coin.code, 0, coin.decimal)
        coinCode.text = coin.title

        btnApprove.setOnSingleClickListener {
            viewModel.onApprove()
        }


        viewModel.feeCoinValue.observe(viewLifecycleOwner, Observer {
            feeValue.text = it
        })


    }

    companion object {
        fun newInstance(coin: Coin, amount: BigDecimal, spenderAddress: String): SwapApproveFragment {
            return SwapApproveFragment().apply {
                arguments = Bundle(3).apply {
                    putParcelable("coin", coin)
                    putSerializable("amount", amount)
                    putString("spenderAddress", spenderAddress)
                }
            }
        }
    }
}
