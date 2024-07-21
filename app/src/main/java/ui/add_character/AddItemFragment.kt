package ui.add_character

import android.app.AlertDialog
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
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
import com.example.couponchest.databinding.FragmentAddItemBinding
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.Text
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import data.model.Item
import data.viewModel.ItemsViewModel
import java.io.IOException
import java.util.regex.Pattern

class AddItemFragment : Fragment() {

    private var _binding: FragmentAddItemBinding? = null
    private val binding get() = _binding!!
    private var imageUri: Uri? = Uri.parse("android.resource://com.example.couponchest/drawable/logo")

    private val viewModel: ItemsViewModel by activityViewModels()

    private val pickImageLauncher: ActivityResultLauncher<Array<String>> =
        registerForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
            if (uri != null) {
                binding.resultImage.setImageURI(uri)
                requireActivity().contentResolver.takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION)
                imageUri = uri
            }
        }

    private val pickImageForMLKitLauncher: ActivityResultLauncher<Array<String>> =
        registerForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
            if (uri != null) {
                try {
                    val imageBitmap = MediaStore.Images.Media.getBitmap(requireActivity().contentResolver, uri)
                    recognizeTextFromImage(imageBitmap)
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAddItemBinding.inflate(inflater, container, false)

        binding.btnFinish.setOnClickListener {
            val title = binding.itemTitle.text.toString()
            val description = binding.itemDescription.text.toString()

            if (title.isEmpty() || description.isEmpty()) {
                Toast.makeText(requireContext(), "Please fill out all fields.", Toast.LENGTH_SHORT).show()
            } else {
                val item = Item(title, description, imageUri.toString())
                viewModel.addItem(item)
                findNavController().navigate(R.id.action_addItemFragment_to_allItemsFragment)
            }
        }

        binding.imageBtn.setOnClickListener {
            pickImageLauncher.launch(arrayOf("image/*"))
        }

        binding.addMlkitItem.setOnClickListener {
            pickImageForMLKitLauncher.launch(arrayOf("image/*"))
        }

        return binding.root
    }

    private fun recognizeTextFromImage(imageBitmap: Bitmap) {
        val image = InputImage.fromBitmap(imageBitmap, 0)
        val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

        recognizer.process(image)
            .addOnSuccessListener { visionText ->
                processTextRecognitionResult(visionText)
            }
            .addOnFailureListener { e ->
                e.printStackTrace()
            }
    }

    private fun processTextRecognitionResult(result: Text) {
        val recognizedText = StringBuilder()

        val patterns = listOf(
            Pattern.compile("\\b\\d{20}\\b"),
            Pattern.compile("\\b[A-Z0-9]{8}\\b"),
            Pattern.compile("\\b[A-Z0-9]{10}\\b"),
            Pattern.compile("\\b[A-Z0-9]{15}\\b"),
            Pattern.compile("\\b[A-Z0-9]{8}-[A-Z0-9]{4}-[A-Z0-9]{4}-[A-Z0-9]{4}-[A-Z0-9]{12}\\b")
        )

        var isPromoCodeRecognized = false

        for (block in result.textBlocks) {
            for (line in block.lines) {
                for (element in line.elements) {
                    val elementText = element.text
                    for (pattern in patterns) {
                        val matcher = pattern.matcher(elementText)
                        if (matcher.matches()) {
                            recognizedText.append(elementText).append(" ")
                            isPromoCodeRecognized = true
                            break
                        }
                    }
                }
            }
        }

        if (recognizedText.isNotEmpty()) {
            binding.itemDescription.append(recognizedText.toString().trim())
        } else {
            val supportedCodes =
                getString(R.string.plese_insert_a_valid_promo_code_supported_promo_code_formats_20_digit_numeric_code_e_g_12345678901234567890_8_character_alphanumeric_code_e_g_abcd1234_10_character_alphanumeric_code_e_g_abcd123456_15_character_alphanumeric_code_e_g_abcdefghijklmno_uuid_like_code_e_g_abcd1234_ab12_cd34_ef56_1234567890ab).trimIndent().replace("-", "\n-")
            val dialogBuilder = AlertDialog.Builder(requireContext())
            dialogBuilder
                .setTitle(getString(R.string.unsupported_promo_code))
                .setMessage("\n\n$supportedCodes")
                .setPositiveButton(getString(R.string.ok)) { dialog, _ ->
                    dialog.dismiss()
                }
                .create()
                .show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
