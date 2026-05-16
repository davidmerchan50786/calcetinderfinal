package com.example.calcetinder.ui.matches

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.calcetinder.R
import com.example.calcetinder.datos.CalcetinderDB
import com.example.calcetinder.datos.Repositorio
import com.example.calcetinder.modelo.Calcetin
import com.example.calcetinder.modelo.Match
import kotlinx.coroutines.launch

class MatchesFragment : Fragment() {

    override fun onCreateView(i: LayoutInflater, c: ViewGroup?, s: Bundle?): View =
        i.inflate(R.layout.fragment_matches, c, false)

    override fun onViewCreated(view: View, s: Bundle?) {
        super.onViewCreated(view, s)
        val db = CalcetinderDB.obtenerDB(requireContext())
        val repo = Repositorio(db.usuarioDAO(), db.calcetinDAO(), db.matchDAO())
        val prefs = requireContext().getSharedPreferences("calcetinder", Context.MODE_PRIVATE)
        val uid = prefs.getInt("usuarioId", -1)
        val rv = view.findViewById<RecyclerView>(R.id.rvMatches)
        rv.layoutManager = LinearLayoutManager(requireContext())
        val adapter = MatchAdapter(emptyList())
        rv.adapter = adapter
        lifecycleScope.launch {
            repo.obtenerMatchesPorUsuario(uid).collect { lista ->
                val pares = lista.map { m ->
                    val otherId = if (m.usuarioId1 == uid) m.usuarioId2 else m.usuarioId1
                    val calcetin = repo.obtenerCalcetinesPorUsuario(otherId).let { flow ->
                        var c: Calcetin? = null
                        flow.collect { c = it.firstOrNull() }
                        c
                    }
                    Pair(m, calcetin)
                }
                adapter.actualizar(pares)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        (activity as? AppCompatActivity)?.supportActionBar?.title = "Matches"
    }

    inner class MatchAdapter(private var items: List<Pair<Match, Calcetin?>>) :
        RecyclerView.Adapter<MatchAdapter.VH>() {

        inner class VH(v: View) : RecyclerView.ViewHolder(v)

        override fun onCreateViewHolder(p: ViewGroup, t: Int): VH =
            VH(LayoutInflater.from(p.context).inflate(R.layout.item_match, p, false))

        override fun onBindViewHolder(h: VH, pos: Int) {
            val (_, c) = items[pos]
            h.itemView.findViewById<TextView>(R.id.tvMatchNombre).text = c?.nombre ?: "Calcetin desconocido"
            h.itemView.findViewById<TextView>(R.id.tvMatchDescripcion).text = c?.descripcion ?: ""
            h.itemView.findViewById<TextView>(R.id.tvMatchColor).text = if (c != null) "Color: ${c.color}" else ""
            h.itemView.findViewById<TextView>(R.id.tvMatchMaterial).text = if (c != null) "Mat: ${c.material}" else ""
        }

        override fun getItemCount() = items.size
        fun actualizar(list: List<Pair<Match, Calcetin?>>) { items = list; notifyDataSetChanged() }
    }
}
