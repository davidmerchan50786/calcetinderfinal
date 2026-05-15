package com.example.calcetinder.ui.miscalcetines

import android.app.AlertDialog
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.calcetinder.R
import com.example.calcetinder.datos.CalcetinderDB
import com.example.calcetinder.datos.Repositorio
import com.example.calcetinder.databinding.FragmentMiscalcetinesBinding
import com.example.calcetinder.modelo.Calcetin
import kotlinx.coroutines.launch

class MisCalcetinesFragment : Fragment() {
    private var _binding: FragmentMiscalcetinesBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: MisCalcetinesViewModel
    private lateinit var adapter: CalcetinAdapter
    private var usuarioId: Int = 0
    private var imagenUriSeleccionada: String = ""
    private var ivDialogo: ImageView? = null

    private val pickImage = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            requireContext().contentResolver.takePersistableUriPermission(
                it, android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION
            )
            imagenUriSeleccionada = it.toString()
            ivDialogo?.let { iv -> Glide.with(this).load(it).centerCrop().into(iv) }
        }
    }

    override fun onCreateView(i: LayoutInflater, c: ViewGroup?, s: Bundle?): View {
        _binding = FragmentMiscalcetinesBinding.inflate(i, c, false)
        return binding.root
    }

    override fun onViewCreated(view: View, s: Bundle?) {
        super.onViewCreated(view, s)
        setHasOptionsMenu(true)
        val prefs = requireContext().getSharedPreferences("calcetinder", Context.MODE_PRIVATE)
        usuarioId = prefs.getInt("usuarioId", 0)
        val db = CalcetinderDB.obtenerDB(requireContext())
        val repo = Repositorio(db.usuarioDAO(), db.calcetinDAO(), db.matchDAO())
        viewModel = ViewModelProvider(this, object : ViewModelProvider.Factory {
            override fun <T : androidx.lifecycle.ViewModel> create(mc: Class<T>): T =
                MisCalcetinesViewModel(repo) as T
        }).get(MisCalcetinesViewModel::class.java)

        adapter = CalcetinAdapter(
            calcetines = emptyList(),
            onDelete = { c -> mostrarDialogoEliminar(c) },
            onEdit   = { c -> mostrarDialogoEditar(c) }
        )
        binding.rvCalcetines.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = this@MisCalcetinesFragment.adapter
        }

        binding.btnCrearCalcetin.setOnClickListener { mostrarDialogoCrear() }

        viewModel.cargarCalcetinesPorUsuario(usuarioId)
        lifecycleScope.launch { viewModel.misCalcetines.collect { adapter.actualizarLista(it) } }
    }

    private fun mostrarDialogoCrear() {
        imagenUriSeleccionada = ""
        val dialogView = LayoutInflater.from(requireContext())
            .inflate(R.layout.dialog_crear_calcetin, null)
        val etNombre      = dialogView.findViewById<EditText>(R.id.et_nombre)
        val etDescripcion = dialogView.findViewById<EditText>(R.id.et_descripcion)
        val etColor       = dialogView.findViewById<EditText>(R.id.et_color)
        val etMaterial    = dialogView.findViewById<EditText>(R.id.et_material)
        val ivPreview     = dialogView.findViewById<ImageView>(R.id.iv_preview)
        ivDialogo = ivPreview
        dialogView.findViewById<android.widget.Button>(R.id.btn_seleccionar_imagen)
            .setOnClickListener { pickImage.launch("image/*") }

        val dialog = AlertDialog.Builder(requireContext())
            .setTitle("Nuevo Calcetin")
            .setView(dialogView)
            .setPositiveButton("Guardar", null)
            .setNegativeButton("Cancelar", null)
            .show()

        // Sobreescribir el boton para que no cierre si hay errores de validacion
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
            val nombre   = etNombre.text.toString().trim()
            val desc     = etDescripcion.text.toString().trim()
            val color    = etColor.text.toString().trim()
            val material = etMaterial.text.toString().trim()
            when {
                nombre.isEmpty() ->
                    etNombre.error = "El nombre es obligatorio"
                nombre.length < 2 ->
                    etNombre.error = "El nombre debe tener al menos 2 caracteres"
                else -> {
                    viewModel.crearCalcetin(usuarioId, nombre, desc, color, material, imagenUriSeleccionada)
                    Toast.makeText(context, getString(R.string.toast_guardado), Toast.LENGTH_SHORT).show()
                    dialog.dismiss()
                }
            }
        }
    }

    private fun mostrarDialogoEditar(c: Calcetin) {
        val dialogView = LayoutInflater.from(requireContext())
            .inflate(R.layout.dialog_crear_calcetin, null)
        val etNombre      = dialogView.findViewById<EditText>(R.id.et_nombre)
        val etDescripcion = dialogView.findViewById<EditText>(R.id.et_descripcion)
        val etColor       = dialogView.findViewById<EditText>(R.id.et_color)
        val etMaterial    = dialogView.findViewById<EditText>(R.id.et_material)
        val ivPreview     = dialogView.findViewById<ImageView>(R.id.iv_preview)
        imagenUriSeleccionada = c.imagenUri
        ivDialogo = ivPreview
        etNombre.setText(c.nombre)
        etDescripcion.setText(c.descripcion)
        etColor.setText(c.color)
        etMaterial.setText(c.material)
        if (c.imagenUri.isNotEmpty()) Glide.with(this).load(c.imagenUri).centerCrop().into(ivPreview)
        dialogView.findViewById<android.widget.Button>(R.id.btn_seleccionar_imagen)
            .setOnClickListener { pickImage.launch("image/*") }

        val dialog = AlertDialog.Builder(requireContext())
            .setTitle("Editar Calcetin")
            .setView(dialogView)
            .setPositiveButton("Guardar", null)
            .setNegativeButton("Cancelar", null)
            .show()

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
            val nombre   = etNombre.text.toString().trim()
            val desc     = etDescripcion.text.toString().trim()
            val color    = etColor.text.toString().trim()
            val material = etMaterial.text.toString().trim()
            when {
                nombre.isEmpty() ->
                    etNombre.error = "El nombre es obligatorio"
                nombre.length < 2 ->
                    etNombre.error = "El nombre debe tener al menos 2 caracteres"
                else -> {
                    viewModel.actualizarCalcetin(
                        c.copy(nombre = nombre, descripcion = desc,
                               color = color, material = material,
                               imagenUri = imagenUriSeleccionada)
                    )
                    Toast.makeText(context, getString(R.string.toast_actualizado), Toast.LENGTH_SHORT).show()
                    dialog.dismiss()
                }
            }
        }
    }

    private fun mostrarDialogoEliminar(c: Calcetin) {
        AlertDialog.Builder(requireContext())
            .setTitle("Eliminar calcetin")
            .setMessage("Seguro que quieres eliminar \"${c.nombre}\"?")
            .setPositiveButton("Eliminar") { _, _ ->
                viewModel.eliminarCalcetin(c)
                Toast.makeText(context, "\"${c.nombre}\" eliminado. Como si nunca hubiera existido.", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    override fun onCreateOptionsMenu(menu: Menu, inf: MenuInflater) {
        inf.inflate(R.menu.menu_misc_calcetines, menu)
        super.onCreateOptionsMenu(menu, inf)
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.action_volver -> { findNavController().navigateUp(); true }
        else -> super.onOptionsItemSelected(item)
    }

    override fun onDestroyView() { super.onDestroyView(); _binding = null }
}
