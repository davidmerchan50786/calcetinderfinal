package com.example.calcetinder.ui.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.calcetinder.R
import com.example.calcetinder.datos.CalcetinderDB
import com.example.calcetinder.datos.Repositorio
import com.example.calcetinder.databinding.FragmentRegistroBinding
import kotlinx.coroutines.launch

class RegistroFragment : Fragment() {
    private var _binding: FragmentRegistroBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: LoginViewModel
    private var registrando = false

    override fun onCreateView(i: LayoutInflater, c: ViewGroup?, s: Bundle?): View {
        _binding = FragmentRegistroBinding.inflate(i, c, false)
        return binding.root
    }

    override fun onViewCreated(view: View, s: Bundle?) {
        super.onViewCreated(view, s)
        val db = CalcetinderDB.obtenerDB(requireContext())
        val repo = Repositorio(db.usuarioDAO(), db.calcetinDAO(), db.matchDAO())
        viewModel = ViewModelProvider(this, object : ViewModelProvider.Factory {
            override fun <T : androidx.lifecycle.ViewModel> create(mc: Class<T>): T = LoginViewModel(repo) as T
        }).get(LoginViewModel::class.java)

        binding.btnRegistrar.setOnClickListener {
            val n = binding.etNombre.text.toString().trim()
            val e = binding.etEmail.text.toString().trim()
            val p = binding.etContrasena.text.toString()
            val c = binding.etCiudad.text.toString().trim()
            when {
                n.isEmpty() || e.isEmpty() || p.isEmpty() || c.isEmpty() ->
                    Toast.makeText(context, getString(R.string.toast_campos_vacios), Toast.LENGTH_SHORT).show()
                !android.util.Patterns.EMAIL_ADDRESS.matcher(e).matches() ->
                    Toast.makeText(context, getString(R.string.toast_email_invalido), Toast.LENGTH_SHORT).show()
                p.length < 6 ->
                    Toast.makeText(context, getString(R.string.toast_contrasena_corta), Toast.LENGTH_SHORT).show()
                n.length < 2 ->
                    Toast.makeText(context, getString(R.string.toast_nombre_corto), Toast.LENGTH_SHORT).show()
                else -> {
                    registrando = true
                    viewModel.registro(n, e, p, c)
                }
            }
        }
        binding.btnVolver.setOnClickListener { findNavController().navigateUp() }

        lifecycleScope.launch {
            viewModel.usuarioActual.collect { u ->
                if (u != null && registrando) {
                    registrando = false
                    Toast.makeText(context, getString(R.string.toast_registro_ok), Toast.LENGTH_SHORT).show()
                    findNavController().navigateUp()
                }
            }
        }
        lifecycleScope.launch {
            viewModel.error.collect { err ->
                if (err.isNotEmpty()) {
                    registrando = false
                    Toast.makeText(context, err, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        (activity as? AppCompatActivity)?.supportActionBar?.hide()
    }

    override fun onStop() {
        super.onStop()
        (activity as? AppCompatActivity)?.supportActionBar?.show()
    }

    override fun onDestroyView() { super.onDestroyView(); _binding = null }
}
