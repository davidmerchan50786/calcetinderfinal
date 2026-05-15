package com.example.calcetinder.ui.miscalcetines

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.calcetinder.R
import com.example.calcetinder.databinding.ItemCalcetinBinding
import com.example.calcetinder.modelo.Calcetin

class CalcetinAdapter(
    private var calcetines: List<Calcetin>,
    private val onDelete: (Calcetin) -> Unit,
    private val onEdit: (Calcetin) -> Unit
) : RecyclerView.Adapter<CalcetinAdapter.VH>() {

    inner class VH(private val b: ItemCalcetinBinding) : RecyclerView.ViewHolder(b.root) {
        fun bind(c: Calcetin) {
            b.tvNombre.text = c.nombre
            b.tvDescripcion.text = c.descripcion
            b.tvColor.text = "Color: " + c.color
            b.tvMaterial.text = "Material: " + c.material
            if (c.imagenUri.isNotEmpty()) {
                Glide.with(b.ivCalcetin.context)
                    .load(c.imagenUri)
                    .centerCrop()
                    .placeholder(R.color.green_500)
                    .into(b.ivCalcetin)
            } else {
                b.ivCalcetin.setImageDrawable(null)
                b.ivCalcetin.setBackgroundColor(
                    b.ivCalcetin.context.getColor(R.color.green_500)
                )
            }
            b.btnEliminar.setOnClickListener { onDelete(c) }
            b.btnEditar.setOnClickListener { onEdit(c) }
        }
    }

    override fun onCreateViewHolder(p: ViewGroup, t: Int) =
        VH(ItemCalcetinBinding.inflate(LayoutInflater.from(p.context), p, false))

    override fun onBindViewHolder(h: VH, pos: Int) = h.bind(calcetines[pos])
    override fun getItemCount() = calcetines.size
    fun actualizarLista(nueva: List<Calcetin>) { calcetines = nueva; notifyDataSetChanged() }
}
