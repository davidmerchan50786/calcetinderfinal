package com.example.calcetinder.ui.login

import android.content.Context
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
import com.example.calcetinder.databinding.FragmentLoginBinding
import kotlinx.coroutines.launch

class LoginFragment : Fragment() {
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: LoginViewModel

    override fun onCreateView(i: LayoutInflater, c: ViewGroup?, s: Bundle?): View {
        _binding = FragmentLoginBinding.inflate(i, c, false)
        return binding.root
    }

    override fun onViewCreated(view: View, s: Bundle?) {
        super.onViewCreated(view, s)

        // Auto-login: si ya hay sesion activa saltar directo al swipe
        val prefs = requireContext().getSharedPreferences("calcetinder", Context.MODE_PRIVATE)
        if (prefs.getInt("usuarioId", -1) > 0) {
            findNavController().navigate(R.id.action_loginFragment_to_swipeFragment)
            return
        }

        val db = CalcetinderDB.obtenerDB(requireContext())
        val repo = Repositorio(db.usuarioDAO(), db.calcetinDAO(), db.matchDAO())
        viewModel = ViewModelProvider(this, object : ViewModelProvider.Factory {
            override fun <T : androidx.lifecycle.ViewModel> create(mc: Class<T>): T = LoginViewModel(repo) as T
        }).get(LoginViewModel::class.java)

        binding.btnLogin.setOnClickListener {
            val e = binding.etEmail.text.toString().trim()
            val p = binding.etContrasena.text.toString()
            when {
                e.isEmpty() || p.isEmpty() ->
                    Toast.makeText(context, getString(R.string.toast_campos_vacios), Toast.LENGTH_SHORT).show()
                !e.contains("@") ->
                    Toast.makeText(context, getString(R.string.toast_email_invalido), Toast.LENGTH_SHORT).show()
                p.length < 6 ->
                    Toast.makeText(context, getString(R.string.toast_contrasena_corta), Toast.LENGTH_SHORT).show()
                else -> viewModel.login(e, p)
            }
        }
        binding.btnRegistro.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_registroFragment)
        }
        lifecycleScope.launch {
            viewModel.usuarioActual.collect { u ->
                if (u != null) {
                    prefs.edit().putInt("usuarioId", u.id).apply()
                    findNavController().navigate(R.id.action_loginFragment_to_swipeFragment)
                }
            }
        }
        lifecycleScope.launch {
            viewModel.error.collect { err ->
                if (err.isNotEmpty()) Toast.makeText(context, err, Toast.LENGTH_SHORT).show()
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
