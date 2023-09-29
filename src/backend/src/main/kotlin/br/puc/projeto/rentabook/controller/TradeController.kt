package br.puc.projeto.rentabook.controller

import br.puc.projeto.rentabook.dto.SaleForm
import br.puc.projeto.rentabook.dto.SaleView
import br.puc.projeto.rentabook.dto.TradeForm
import br.puc.projeto.rentabook.dto.TradeView
import br.puc.projeto.rentabook.model.Sale
import br.puc.projeto.rentabook.service.SaleService
import br.puc.projeto.rentabook.service.TradeService
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/sales")
class TradeController(private val tradeService: TradeService) {



    @PutMapping("{id}/undo")
    fun cancel(@PathVariable id: String): TradeView {
        return tradeService.cancel(id)
    }

}
