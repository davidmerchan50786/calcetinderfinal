package com.example.calcetinder.ui.swipe

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.calcetinder.R
import com.example.calcetinder.datos.CalcetinderDB
import com.example.calcetinder.datos.Repositorio
import com.example.calcetinder.databinding.FragmentSwipeBinding
import kotlinx.coroutines.launch

class SwipeFragment : Fragment() {
    private var _binding: FragmentSwipeBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: SwipeViewModel
    private var usuarioId: Int = 0

    override fun onCreateView(i: LayoutInflater, c: ViewGroup?, s: Bundle?): View {
        _binding = FragmentSwipeBinding.inflate(i, c, false)
        return binding.root
    }

    override fun onViewCreated(view: View, s: Bundle?) {
        super.onViewCreated(view, s)
        val prefs = requireContext().getSharedPreferences("calcetinder", Context.MODE_PRIVATE)
        usuarioId = prefs.getInt("usuarioId", 0)
        val db = CalcetinderDB.obtenerDB(requireContext())
        val repo = Repositorio(db.usuarioDAO(), db.calcetinDAO(), db.matchDAO())
        viewModel = ViewModelProvider(this, object : ViewModelProvider.Factory {
            override fun <T : androidx.lifecycle.ViewModel> create(mc: Class<T>): T = SwipeViewModel(repo) as T
        }).get(SwipeViewModel::class.java)
        setHasOptionsMenu(true)
        viewModel.cargarCalcetines(usuarioId)
        binding.btnLike.setOnClickListener { viewModel.like(usuarioId) }
        binding.btnDislike.setOnClickListener { viewModel.dislike() }
        lifecycleScope.launch { viewModel.calcetines.collect { actualizarUI() } }
        lifecycleScope.launch { viewModel.indiceActual.collect { actualizarUI() } }
    }

    private fun actualizarUI() {
        val idx = viewModel.indiceActual.value
        val lista = viewModel.calcetines.value
        if (lista.isNotEmpty() && idx >= 0 && idx < lista.size) {
            val c = lista[idx]
            binding.tvNombre.text = c.nombre
            binding.tvDescripcion.text = c.descripcion
            binding.tvColor.text = "Color: " + c.color
            binding.tvMaterial.text = "Material: " + c.material
            if (c.imagenUri.isNotEmpty()) {
                Glide.with(this).load(c.imagenUri).centerCrop()
                    .placeholder(R.color.green_500).into(binding.ivCalcetin)
            } else {
                binding.ivCalcetin.setImageDrawable(null)
                binding.ivCalcetin.setBackgroundColor(requireContext().getColor(R.color.green_500))
            }
        } else {
            binding.tvNombre.text = getString(R.string.swipe_vacio_titulo)
            binding.tvDescripcion.text = getString(R.string.swipe_vacio_desc)
            binding.tvColor.text = ""
            binding.tvMaterial.text = ""
            binding.ivCalcetin.setImageDrawable(null)
            binding.ivCalcetin.setBackgroundColor(requireContext().getColor(R.color.green_500))
            binding.btnLike.isEnabled = false
            binding.btnDislike.isEnabled = false
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inf: MenuInflater) {
        inf.inflate(R.menu.menu_swipe, menu)
        super.onCreateOptionsMenu(menu, inf)
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.action_mis_calcetines -> {
            findNavController().navigate(R.id.action_swipeFragment_to_miscalcetinesFragment); true
        }
        R.id.action_matches -> {
            findNavController().navigate(R.id.action_swipeFragment_to_matchesFragment); true
        }
        R.id.action_logout -> {
            requireContext().getSharedPreferences("calcetinder", Context.MODE_PRIVATE)
                .edit().clear().apply()
            findNavController().navigate(R.id.action_swipeFragment_to_loginFragment)
            true
        }
        else -> super.onOptionsItemSelected(item)
    }

    override fun onDestroyView() { super.onDestroyView(); _binding = null }
}
