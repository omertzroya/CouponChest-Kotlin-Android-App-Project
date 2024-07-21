package ui.single_character

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.couponchest.R
import com.example.couponchest.databinding.FragmentEditItemBinding
import data.viewModel.ItemsViewModel
import data.model.Item

class EditItemFragment : Fragment() {

    private val viewModel: ItemsViewModel by activityViewModels()
    private var item: Item? = null
    private var selectedImageUri: Uri = Uri.parse("android.resource://com.example.couponchest/drawable/logo")

    private var _binding: FragmentEditItemBinding? = null
    private val binding get() = _binding!!

    private val pickImageLauncher: ActivityResultLauncher<Array<String>> =
        registerForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
            uri?.let { selectedUri ->
                binding.imageView.setImageURI(selectedUri)
                requireActivity().contentResolver.takePersistableUriPermission(selectedUri, Intent.FLAG_GRANT_READ_URI_PERMISSION)
                selectedImageUri = selectedUri
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEditItemBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupViews()
        observeSelectedItem()
        setupClickListeners()
    }

    private fun setupViews() {
        item?.let { currentItem ->
            binding.editTitle.setText(currentItem.title)
            binding.editDescription.setText(currentItem.description)
            selectedImageUri = Uri.parse(currentItem.photo)
            binding.imageView.setImageURI(selectedImageUri)
        }
    }

    private fun observeSelectedItem() {
        viewModel.selectedItem.observe(viewLifecycleOwner) { selectedItem ->
            item = selectedItem
            setupViews()
        }
    }

    private fun setupClickListeners() {
        binding.editImage.setOnClickListener {
            pickImageLauncher.launch(arrayOf("image/*"))
        }

        binding.save.setOnClickListener {
            val newTitle = binding.editTitle.text.toString()
            val newDescription = binding.editDescription.text.toString()

            if (newTitle.isNotEmpty() && newDescription.isNotEmpty()) {
                item?.let { currentItem ->
                    currentItem.title = newTitle
                    currentItem.description = newDescription
                    currentItem.photo = selectedImageUri.toString()

                    viewModel.updateItem(currentItem)
                    Toast.makeText(requireContext(),
                        getString(R.string.item_updated), Toast.LENGTH_SHORT).show()
                    findNavController().navigateUp()
                }
            } else {
                Toast.makeText(requireContext(),
                    getString(R.string.title_and_description_cannot_be_empty), Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
