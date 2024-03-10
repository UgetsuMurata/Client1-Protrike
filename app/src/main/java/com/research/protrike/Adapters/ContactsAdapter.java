package com.research.protrike.Adapters;

import android.content.Context;
import android.content.Intent;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.research.protrike.Application.Protrike.ContactHolder;
import com.research.protrike.HelperFunctions.CharacterCode;
import com.research.protrike.MainFeats.Contacts.NewContact;
import com.research.protrike.R;

import java.util.ArrayList;
import java.util.List;

public class ContactsAdapter extends RecyclerView.Adapter<ContactsAdapter.ContactsAdapterHolder> {

    Context context;
    ContactHolder contactHolder;
    MessageCallback callback;
    List<String> defaultContactsList;

    public interface MessageCallback {
        void SendMessage(String name, String number, String message);
    }

    public ContactsAdapter(Context context, ContactHolder contactHolder, List<String> defaultContactsList) {
        this.context = context;
        this.contactHolder = contactHolder;
        this.defaultContactsList = defaultContactsList;
    }


    @NonNull
    @Override
    public ContactsAdapterHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recyclerview_emergency_alert, parent, false);
        return new ContactsAdapterHolder(view);
    }

    public void onSendMessage(MessageCallback messageCallback){
        this.callback = messageCallback;
    }

    @Override
    public void onBindViewHolder(@NonNull ContactsAdapterHolder holder, int position) {
        final String name = contactHolder.get(position).getName();
        final String number = contactHolder.get(position).getNumber();
        final String message = CharacterCode.messageEncode(contactHolder.get(position).getMessage());
        final int current_position = position;

        holder.contactName.setText(name);
        holder.contactNumber.setText(number);
        holder.contactMessage.setText(message);

        // hide edit and delete buttons for default numbers.
        if (defaultContactsList.contains(name)){
            holder.deleteMessage.setVisibility(View.GONE);
            holder.editMessage.setVisibility(View.GONE);
            return;
        }

        holder.deleteMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ContactHolder oldContactHolder = new ContactHolder(contactHolder);
                oldContactHolder.remove(current_position);
                contactHolder.clear();
                contactHolder.addAll(contactHolder);
                notifyItemRemoved(current_position);
            }
        });

        holder.editMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, NewContact.class);
                intent.putExtra("CONTACT_NAME", name);
                intent.putExtra("CONTACT_NUMBER", number);
                intent.putExtra("CONTACT_MESSAGE", message);
                context.startActivity(intent);
            }
        });

        holder.sendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callback.SendMessage(name, number, message);
            }
        });
    }

    @Override
    public int getItemCount() {
        return contactHolder.size();
    }
    public static class ContactsAdapterHolder extends RecyclerView.ViewHolder{
        TextView contactName, contactNumber, contactMessage;
        ImageView editMessage, sendMessage, deleteMessage;
        public ContactsAdapterHolder(@NonNull View itemView) {
            super(itemView);
            contactName = itemView.findViewById(R.id.contact_name);
            contactNumber = itemView.findViewById(R.id.contact_number);
            contactMessage = itemView.findViewById(R.id.contact_message);
            deleteMessage = itemView.findViewById(R.id.delete_message);
            editMessage = itemView.findViewById(R.id.edit_message);
            sendMessage = itemView.findViewById(R.id.send_message);
        }
    }
}
