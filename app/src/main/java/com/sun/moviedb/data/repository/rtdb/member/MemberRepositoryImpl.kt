package com.sun.moviedb.data.repository.rtdb.member

import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database
import com.google.firebase.database.getValue
import com.sun.moviedb.data.model.Member
import com.sun.moviedb.data.repository.source.remote.NetworkResult
import com.sun.moviedb.utils.MemberListener

class MemberRepositoryImpl : MemberRepository {
    private val memberRef = Firebase.database.reference.child(membersPath)
    private val childListeners = mutableMapOf<String, ChildEventListener>()
    private val valueListeners = mutableMapOf<String, ValueEventListener>()
    private val TAG = "MemberRepositoryImpl"

    override fun addMember(
        roomId: String,
        member: Member,
        onResult: (NetworkResult<Unit>) -> Unit
    ) {
        val memberNode = memberRef.child(roomId).child(member.memberId)

        memberNode.setValue(member)
            .addOnSuccessListener {
                onResult(NetworkResult.OnSuccess(Unit))
                Log.d(
                    TAG,
                    "Member added successfully on addMember(): ${member.memberName} (${member.memberId}) into ($roomId)"
                )

                /* *
                * Ensure that the member node is removed if they disconnect
                *  */
                memberNode.onDisconnect().removeValue()
            }
            .addOnFailureListener { error ->
                onResult(NetworkResult.OnError(null, error.message ?: "Cannot add member"))
                Log.e(TAG, "Failed to add member: ${member.memberId}", error)
            }
    }

    override fun removeMember(
        roomId: String,
        memberId: String,
        onResult: (NetworkResult<Unit>) -> Unit
    ) {
        memberRef.child(roomId).child(memberId).removeValue()
            .addOnSuccessListener {
                onResult(NetworkResult.OnSuccess(Unit))
                Log.d(TAG, "Member removed successfully: $memberId from ($roomId)")
            }
            .addOnFailureListener { error ->
                onResult(
                    NetworkResult.OnError(
                        null,
                        error.message ?: "Cannot remove member from ($roomId)"
                    )
                )
                Log.e(TAG, "Failed to remove member: ($memberId) from ($roomId)", error)
            }
    }

    override fun listenMemberChanged(
        roomId: String,
        onResult: (MemberListener<Member>) -> Unit
    ) {
        val child = object : ChildEventListener {
            override fun onChildAdded(
                snapshot: DataSnapshot,
                previousChildName: String?
            ) {
                try {
                    snapshot.getValue<Member>()?.let { item ->
                        val memberId = snapshot.key
                        onResult(MemberListener.OnJoin(item))
                        Log.d(
                            TAG,
                            "New member added on ChildAdded():${item.memberName} ($memberId) from ($roomId)"
                        )
                    }

                } catch (e: Exception) {
                    onResult(
                        MemberListener.OnError(
                            null,
                            e.message ?: "Error to load all members from ($roomId)"
                        )
                    )
                    Log.e(TAG, "Error receiving members from ($roomId): ${e.message}")
                }
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {}

            override fun onChildRemoved(snapshot: DataSnapshot) {
                try {
                    snapshot.getValue<Member>()?.let {
                        onResult(MemberListener.OnLeave(it))
                        Log.d(TAG, "Member left: ${it.memberName} (${it.memberId}) from ($roomId)")
                    }
                } catch (e: Exception) {
                    onResult(
                        MemberListener.OnError(
                            null,
                            "Failed to parse leaved member data from ($roomId)"
                        )
                    )
                    Log.e(TAG, "Failed to left the member from ($roomId)")
                }
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}

            override fun onCancelled(error: DatabaseError) {}
        }

        val value = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = snapshot.children.mapNotNull { it.getValue(Member::class.java) }
                onResult(MemberListener.onListChanged(list))
            }

            override fun onCancelled(error: DatabaseError) {}
        }

        memberRef.child(roomId).addChildEventListener(child)
        memberRef.child(roomId).addValueEventListener(value)
        childListeners[roomId] = child
        valueListeners[roomId] = value
    }

    override fun removeChildEventListener(roomId: String) {
        childListeners.remove(roomId)?.let { listener ->
            memberRef.child(roomId).removeEventListener(listener)
            Log.d(TAG, "ChildEventListener removed for room: $roomId")
        }
    }

    override fun removeValueEventListener(roomId: String) {
        valueListeners.remove(roomId)?.let { listener ->
            memberRef.child(roomId).removeEventListener(listener)
            Log.d(TAG, "ValueEventListener removed for room: $roomId")
        }
    }

    companion object {
        private const val membersPath = "members"
        private const val createAtPath = "createAt"

        private var instance: MemberRepositoryImpl? = null
        fun getInstance(): MemberRepositoryImpl {
            if (instance == null) {
                instance = MemberRepositoryImpl()
            }
            return instance!!
        }
    }
}

