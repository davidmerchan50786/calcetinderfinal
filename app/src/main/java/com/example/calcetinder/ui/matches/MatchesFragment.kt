package com.example.calcetinder.ui.matches

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.calcetinder.R
import com.example.calcetinder.datos.CalcetinderDB
import com.example.calcetinder.datos.Repositorio
import com.example.calcetinder.databinding.FragmentMatchesBinding
import com.example.calcetinder.databinding.ItemMatchBinding
import com.example.calcetinder.modelo.Calcetin
import com.example.calcetinder.modelo.Match
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class MatchesFragment : Fragment() {
    private var _binding: FragmentMatchesBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(i: LayoutInflater, c: ViewGroup?, s: Bundle?): View {
        _binding = FragmentMatchesBinding.inflate(i, c, false)
        return binding.root
    }

    override fun onViewCreated(view: View, s: Bundle?) {
        super.onViewCreated(view, s)
        setHasOptionsMenu(true)
        val prefs = requireContext().getSharedPreferences("calcetinder", Context.MODE_PRIVATE)
        val usuarioId = prefs.getInt("usuarioId", 0)
        val db = CalcetinderDB.obtenerDB(requireContext())
        val repo = Repositorio(db.usuarioDAO(), db.calcetinDAO(), db.matchDAO())
        val adapter = MatchAdapter(emptyList())
        binding.rvMatches.layoutManager = LinearLayoutManager(requireContext())
        binding.rvMatches.adapter = adapter
        lifecycleScope.launch {
            combine(repo.obtenerMatches(usuarioId), repo.obtenerTodosCalcetines()) { matches, calcetines ->
                matches.map { m -> m to calcetines.find { it.id == m.calcetinId } }
            }.collect { adapter.actualizar(it) }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inf: MenuInflater) {
        inf.inflate(R.menu.menu_matches, menu)
        super.onCreateOptionsMenu(menu, inf)
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.action_volver_matches -> { findNavController().navigateUp(); true }
        else -> super.onOptionsItemSelected(item)
    }

    override fun onDestroyView() { super.onDestroyView(); _binding = null }
}

private class MatchAdapter(private var items: List<Pair<Match, Calcetin?>>) :
    RecyclerView.Adapter<MatchAdapter.VH>() {

    inner class VH(val binding: ItemMatchBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(p: ViewGroup, t: Int) =
        VH(ItemMatchBinding.inflate(LayoutInflater.from(p.context), p, false))

    override fun onBindViewHolder(h: VH, pos: Int) {
        val (_, c) = items[pos]
        h.binding.tvMatchNombre.text = c?.nombre ?: "Calcetin desconocido"
        h.binding.tvMatchDescripcion.text = c?.descripcion ?: ""
        h.binding.tvMatchColor.text = if (c != null) "Color: ${c.color}" else ""
        h.binding.tvMatchMaterial.text = if (c != null) "Mat: ${c.material}" else ""
    }

    override fun getItemCount() = items.size
    fun actualizar(nueva: List<Pair<Match, Calcetin?>>) { items = nueva; notifyDataSetChanged() }
}
