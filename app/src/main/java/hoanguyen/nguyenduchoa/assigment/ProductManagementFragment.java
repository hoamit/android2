package hoanguyen.nguyenduchoa.assigment;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ProductManagementFragment extends Fragment implements ProductAdapter.OnProductClickListener {

    private RecyclerView rvProducts;
    private FloatingActionButton fabAddProduct;
    private ProductAdapter adapter;
    private List<Product> productList;
    private ImageView ivMenu;
    private DatabaseHelper databaseHelper;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_product_management, container, false);
        
        databaseHelper = new DatabaseHelper(getContext());
        initViews(view);
        setupToolbar(view);
        setupRecyclerView();
        loadProductsFromDatabase();
        setupListeners();
        
        return view;
    }

    private void initViews(View view) {
        rvProducts = view.findViewById(R.id.rvProducts);
        fabAddProduct = view.findViewById(R.id.fabAddProduct);
        ivMenu = view.findViewById(R.id.ivMenu);
    }

    private void setupToolbar(View view) {
        Toolbar toolbar = view.findViewById(R.id.toolbar);
        if (getActivity() instanceof AppCompatActivity) {
            ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
            if (((AppCompatActivity) getActivity()).getSupportActionBar() != null) {
                ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayShowTitleEnabled(false);
            }
        }
    }

    private void setupRecyclerView() {
        productList = new ArrayList<>();
        adapter = new ProductAdapter(productList, this);
        rvProducts.setLayoutManager(new LinearLayoutManager(getContext()));
        rvProducts.setAdapter(adapter);
    }

    private void loadProductsFromDatabase() {
        productList.clear();
        productList.addAll(databaseHelper.getAllProducts());
        adapter.notifyDataSetChanged();
    }

    private void setupListeners() {
        fabAddProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddProductDialog();
            }
        });

        ivMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getActivity() instanceof MainActivity) {
                    DrawerLayout drawerLayout = getActivity().findViewById(R.id.drawer_layout);
                    if (drawerLayout != null) {
                        drawerLayout.openDrawer(GravityCompat.START);
                    }
                }
            }
        });
    }

    private void showAddProductDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_add_edit_product, null);
        builder.setView(dialogView);

        EditText etProductName = dialogView.findViewById(R.id.etProductName);
        EditText etProductPrice = dialogView.findViewById(R.id.etProductPrice);
        EditText etProductQuantity = dialogView.findViewById(R.id.etProductQuantity);
        Button btnAddProduct = dialogView.findViewById(R.id.btnAddProduct);
        TextView tvDialogTitle = dialogView.findViewById(R.id.tvDialogTitle);

        tvDialogTitle.setText("THÊM SẢN PHẨM");
        btnAddProduct.setText("THÊM");
        
        // Clear pre-filled data
        etProductName.setText("");
        etProductPrice.setText("");
        etProductQuantity.setText("");

        AlertDialog dialog = builder.create();

        btnAddProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = etProductName.getText().toString().trim();
                String priceStr = etProductPrice.getText().toString().trim();
                String quantityStr = etProductQuantity.getText().toString().trim();

                if (name.isEmpty() || priceStr.isEmpty() || quantityStr.isEmpty()) {
                    Toast.makeText(getContext(), "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                    return;
                }

                try {
                    int price = Integer.parseInt(priceStr);
                    int quantity = Integer.parseInt(quantityStr);
                    
                    Product newProduct = new Product(name, price, quantity);
                    newProduct.setMaSP(UUID.randomUUID().toString());
                    
                    if (databaseHelper.addProduct(newProduct)) {
                        loadProductsFromDatabase();
                        dialog.dismiss();
                        Toast.makeText(getContext(), "Thêm sản phẩm thành công!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getContext(), "Lỗi khi thêm sản phẩm", Toast.LENGTH_SHORT).show();
                    }
                } catch (NumberFormatException e) {
                    Toast.makeText(getContext(), "Giá và số lượng phải là số", Toast.LENGTH_SHORT).show();
                }
            }
        });

        dialog.show();
    }

    private void showEditProductDialog(Product product, int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_add_edit_product, null);
        builder.setView(dialogView);

        EditText etProductName = dialogView.findViewById(R.id.etProductName);
        EditText etProductPrice = dialogView.findViewById(R.id.etProductPrice);
        EditText etProductQuantity = dialogView.findViewById(R.id.etProductQuantity);
        Button btnAddProduct = dialogView.findViewById(R.id.btnAddProduct);
        TextView tvDialogTitle = dialogView.findViewById(R.id.tvDialogTitle);

        // Pre-fill with existing data
        etProductName.setText(product.getName());
        etProductPrice.setText(String.valueOf(product.getPrice()));
        etProductQuantity.setText(String.valueOf(product.getQuantity()));

        tvDialogTitle.setText("CHỈNH SỬA SẢN PHẨM");
        btnAddProduct.setText("CẬP NHẬT");

        AlertDialog dialog = builder.create();

        btnAddProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = etProductName.getText().toString().trim();
                String priceStr = etProductPrice.getText().toString().trim();
                String quantityStr = etProductQuantity.getText().toString().trim();

                if (name.isEmpty() || priceStr.isEmpty() || quantityStr.isEmpty()) {
                    Toast.makeText(getContext(), "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                    return;
                }

                try {
                    int price = Integer.parseInt(priceStr);
                    int quantity = Integer.parseInt(quantityStr);
                    
                    product.setName(name);
                    product.setPrice(price);
                    product.setQuantity(quantity);
                    
                    if (databaseHelper.updateProduct(product)) {
                        loadProductsFromDatabase();
                        dialog.dismiss();
                        Toast.makeText(getContext(), "Cập nhật sản phẩm thành công!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getContext(), "Lỗi khi cập nhật sản phẩm", Toast.LENGTH_SHORT).show();
                    }
                } catch (NumberFormatException e) {
                    Toast.makeText(getContext(), "Giá và số lượng phải là số", Toast.LENGTH_SHORT).show();
                }
            }
        });

        dialog.show();
    }

    private void showDeleteConfirmDialog(Product product, int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_confirm_delete, null);
        builder.setView(dialogView);

        TextView tvDeleteMessage = dialogView.findViewById(R.id.tvDeleteMessage);
        Button btnCancel = dialogView.findViewById(R.id.btnCancel);
        Button btnConfirm = dialogView.findViewById(R.id.btnConfirm);

        tvDeleteMessage.setText("Bạn có muốn xóa sản phẩm " + product.getName() + "?");

        AlertDialog dialog = builder.create();

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (databaseHelper.deleteProduct(product.getMaSP())) {
                    loadProductsFromDatabase();
                    dialog.dismiss();
                    Toast.makeText(getContext(), "Xóa sản phẩm thành công!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), "Lỗi khi xóa sản phẩm", Toast.LENGTH_SHORT).show();
                }
            }
        });

        dialog.show();
    }

    @Override
    public void onEditClick(Product product, int position) {
        showEditProductDialog(product, position);
    }

    @Override
    public void onDeleteClick(Product product, int position) {
        showDeleteConfirmDialog(product, position);
    }
} 