package ui.all_characters

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.couponchest.R
import com.example.couponchest.databinding.FragmentAllItemsBinding
import data.viewModel.ItemsViewModel

class AllItemsFragment :Fragment(){

    private var _binding : FragmentAllItemsBinding? = null

    private val binding get() = _binding!!

    private val viewModel : ItemsViewModel by activityViewModels()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        _binding = FragmentAllItemsBinding.inflate(inflater,container,false)

        binding.fab.setOnClickListener{
            findNavController().navigate(R.id.action_allItemsFragment_to_addItemFragment)
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.navigateButton.setOnClickListener{
            findNavController().navigate(R.id.action_allItemsFragment_to_mapFragment)
        }


        arguments?.getString(getString(R.string.title))?.let{
            Toast.makeText(requireActivity(),it,Toast.LENGTH_SHORT).show()
        }

        viewModel.items?.observe(viewLifecycleOwner){
            binding.recycler.adapter = ItemAdapter(it,object : ItemAdapter.ItemListener {
                override fun onItemClicked(index: Int) {
                    val item = (binding.recycler.adapter as ItemAdapter).itemAt(index)
                    viewModel.setItem(item)
                    findNavController().navigate(R.id.action_allItemsFragment_to_detailItemFragment)
                }

                override fun onItemLongClicked(index: Int) {
                    val item = (binding.recycler.adapter as ItemAdapter).itemAt(index)
                    viewModel.setItem(item)
                    findNavController().navigate(R.id.action_allItemsFragment_to_editItemFragment)
                }


            })
            binding.recycler.layoutManager = LinearLayoutManager(requireContext())
        }

        ItemTouchHelper(object : ItemTouchHelper.Callback(){
            override fun getMovementFlags(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder
            ) = makeFlag(ItemTouchHelper.ACTION_STATE_SWIPE,ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT)

            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                TODO("Not yet implemented")
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val item = (binding.recycler.adapter as ItemAdapter).itemAt(viewHolder.adapterPosition)


                val builder = AlertDialog.Builder(viewHolder.itemView.context)
                builder.setTitle(viewHolder.itemView.context.getString(R.string.confirm_delete))
                    .setMessage(viewHolder.itemView.context.getString(R.string.are_you_sure_you_want_to_delete_this_item))
                    .setPositiveButton(viewHolder.itemView.context.getString(R.string.yes)) { dialog, id ->
                        viewModel.deleteItem(item)
                    }
                    .setNegativeButton(viewHolder.itemView.context.getString(R.string.no)) { dialog, id ->
                        binding.recycler.adapter!!.notifyItemChanged(viewHolder.adapterPosition)
                    }

                val dialog = builder.create()
                dialog.setOnCancelListener {
                    binding.recycler.adapter!!.notifyItemChanged(viewHolder.adapterPosition)
                }
                dialog.show()

            }

        }).attachToRecyclerView(binding.recycler)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.main_menu,menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_delete) {
            val builder = AlertDialog.Builder(requireContext())
            builder.setTitle(getString(R.string.confirm_delete))
                .setMessage(getString(R.string.are_you_sure_you_want_to_delete_all_items))
                .setPositiveButton(getString(R.string.yes)) { _, _ ->
                    viewModel.deleteAll()
                    Toast.makeText(requireContext(),
                        getString(R.string.items_deleted), Toast.LENGTH_SHORT).show()
                }
                .setNegativeButton(getString(R.string.no)) { dialog, _ ->
                    dialog.dismiss()
                }
                .show()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}