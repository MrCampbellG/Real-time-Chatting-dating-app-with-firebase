package com.naeemdev.realtimechatwithfirebase.ui.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.naeemdev.realtimechatwithfirebase.R;
import com.naeemdev.realtimechatwithfirebase.firestore.ChatsFirestore;
import com.naeemdev.realtimechatwithfirebase.model.MessageClass;
import com.naeemdev.realtimechatwithfirebase.ui.Activity.MessageActivity;


/**
 *
 */
public class ChatsFragment extends Fragment {

    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    LinearLayout linearLayoutChatsContent;
    LinearLayout linearLayoutChatsEmpty;
    private RecyclerView recyclerViewChatsView;
    private ChatsFirestore chatsFirestore;

    /**
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_chat, container, false);
        recyclerViewChatsView = view.findViewById(R.id.recyclerViewChatsView);
        ChatRecyclerView();


        linearLayoutChatsContent = view.findViewById(R.id.linearLayoutChatsContent);
        linearLayoutChatsContent.setVisibility(View.VISIBLE);
        linearLayoutChatsEmpty = view.findViewById(R.id.linearLayoutChatsEmpty);
        linearLayoutChatsEmpty.setVisibility(View.GONE);


        firebaseFirestore.collection("users")
                .document(firebaseUser.getUid())
                .collection("chats")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@javax.annotation.Nullable QuerySnapshot queryDocumentSnapshots, @javax.annotation.Nullable FirebaseFirestoreException e) {
                        if (queryDocumentSnapshots != null) {
                            if (queryDocumentSnapshots.size() == 0) {

                                linearLayoutChatsContent.setVisibility(View.GONE);
                                linearLayoutChatsEmpty.setVisibility(View.VISIBLE);

                            } else {

                                linearLayoutChatsContent.setVisibility(View.VISIBLE);
                                linearLayoutChatsEmpty.setVisibility(View.GONE);
                            }
                        }
                    }
                });


        return view;
    }

    private void ChatRecyclerView() {

        Query query = firebaseFirestore.collection("users")
                .document(firebaseUser.getUid())
                .collection("chats")
                .orderBy("user_datesent", Query.Direction.DESCENDING);

        FirestoreRecyclerOptions<MessageClass> options = new FirestoreRecyclerOptions.Builder<MessageClass>()
                .setQuery(query, MessageClass.class)
                .build();


        chatsFirestore = new ChatsFirestore(options);

        recyclerViewChatsView.setHasFixedSize(true);
        recyclerViewChatsView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerViewChatsView.setAdapter(chatsFirestore);

        chatsFirestore.setOnItemClickListener(new ChatsFirestore.OnItemClickListener() {
            @Override
            public void onItemClick(DocumentSnapshot documentSnapshot, int position) {
                final MessageClass messageClass = documentSnapshot.toObject(MessageClass.class);
                String id = documentSnapshot.getId();
                String path = documentSnapshot.getReference().getPath();
                final Intent intent = new Intent(getContext(), MessageActivity.class);
                intent.putExtra("user_uid", messageClass.getUser_receiver());
                startActivity(intent);
            }
        });


    }


    @Override
    public void onStart() {
        super.onStart();
        chatsFirestore.startListening();
    }


    @Override
    public void onResume() {
        super.onResume();
    }


}
