package com.example.marketer10;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {

    private List<Product> productList;

    public ProductAdapter(List<Product> productList) {
        this.productList = productList;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.product_item, parent, false);

        return new ProductViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = productList.get(position);
        holder.supermarket.setText(product.getSupermarket());
        holder.productType.setText(product.getGoods());
        holder.price.setText(String.valueOf(product.getPrice()) + " EUR");
        holder.unitPrice.setText(String.valueOf(product.getUnitPrice()) + " EUR/kg");
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    class ProductViewHolder extends RecyclerView.ViewHolder {
        TextView supermarket, productType, price, unitPrice;

        ProductViewHolder(View view) {
            super(view);
            supermarket = view.findViewById(R.id.supermarket);
            productType = view.findViewById(R.id.productType);
            price = view.findViewById(R.id.price);
            unitPrice = view.findViewById(R.id.unit_price);
        }
    }
}
