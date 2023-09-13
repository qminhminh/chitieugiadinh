package com.example.chi_tieu_giadinh.adapter;

import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chi_tieu_giadinh.databinding.ItemContainerReviedMessageBinding;
import com.example.chi_tieu_giadinh.databinding.ItemContainerSendMessageBinding;
import com.example.chi_tieu_giadinh.model.ChatMessageModel;

import java.util.ArrayList;

public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    ArrayList<ChatMessageModel> chatMessages;
    private Bitmap receiveProfileImag;
    private String senderId;
    int VIEW_TYPE_SENT = 1;
    int VIEW_TYPE_RECEIVED = 2;

    public ChatAdapter(ArrayList<ChatMessageModel> chatMessages, Bitmap receiveProfileImag, String senderId) {
        this.chatMessages = chatMessages;
        this.receiveProfileImag = receiveProfileImag;
        this.senderId = senderId;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType==VIEW_TYPE_SENT){
            return new SentMessageViewHolder(
                    ItemContainerSendMessageBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false));

        }
        else {
            return new ReceivedMessageViewHolder(
                    ItemContainerReviedMessageBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false)
            );
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if(getItemViewType(position)==VIEW_TYPE_SENT){
            ((SentMessageViewHolder) holder).setData(chatMessages.get(position));

        }else {
            ((ReceivedMessageViewHolder) holder).setData(chatMessages.get(position),receiveProfileImag );
        }
    }

    @Override
    public int getItemCount() {
        return chatMessages.size();
    }
    @Override
    public int getItemViewType(int position) {
        ChatMessageModel chatMessage = chatMessages.get(position);
        if (chatMessage == null) {
            return 0;//Nếu tin nhắn là null, trả về giá trị 0.
        } else if (chatMessage.senderId.equals(senderId)) {
            return VIEW_TYPE_SENT;//Nếu tin nhắn được gửi từ người gửi, trả về VIEW_TYPE_SENT.
        } else {
            return VIEW_TYPE_RECEIVED;//Ngược lại, trả về VIEW_TYPE_RECEIVED.
        }
    }


    static class SentMessageViewHolder extends RecyclerView.ViewHolder {

        ItemContainerSendMessageBinding binding;

        public SentMessageViewHolder(ItemContainerSendMessageBinding itemContainerSendMessageBinding) {
            super(itemContainerSendMessageBinding.getRoot());
            binding = itemContainerSendMessageBinding;
        }

        void setData(ChatMessageModel chatMessage) {
            binding.textMessage.setText(chatMessage.message);
            binding.textDateTime.setText(chatMessage.dateTime);

        }
    }
    static class ReceivedMessageViewHolder extends RecyclerView.ViewHolder {
        ItemContainerReviedMessageBinding binding;

        public ReceivedMessageViewHolder(ItemContainerReviedMessageBinding itemContainerReviedMessageBinding) {
            super(itemContainerReviedMessageBinding.getRoot());
            binding = itemContainerReviedMessageBinding;
        }

        void setData(ChatMessageModel chatMessage, Bitmap receiveProfileImag) {
            binding.textMessage.setText(chatMessage.message);
            binding.textDateTime.setText(chatMessage.dateTime);
            binding.imgeProfile.setImageBitmap(receiveProfileImag);
        }
    }
}



