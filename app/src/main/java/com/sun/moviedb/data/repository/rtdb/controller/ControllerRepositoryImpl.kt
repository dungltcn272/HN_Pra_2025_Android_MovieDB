package com.sun.moviedb.data.repository.impl

import android.util.Log
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.sun.moviedb.data.repository.ControllerRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class ControllerRepositoryImpl(
    private val database: FirebaseDatabase
) : ControllerRepository {

    private val roomsRef = database.reference.child("rooms")
    private val TAG = "ControllerRepository"

    override fun listenForRoomCommandString(roomId: String): Flow<String?> = callbackFlow {
        if (roomId.isBlank()) {
            Log.w(TAG, "Room ID is blank for listening to commands.")
            trySend(null)
            close(IllegalArgumentException("Room ID cannot be blank"))
            return@callbackFlow
        }

        val commandNodeRef = roomsRef.child(roomId).child("controller_command")

        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val commandString = snapshot.getValue(String::class.java)
                Log.d(TAG, "Room ($roomId) command string received: $commandString")
                trySend(commandString)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e(TAG, "Error listening for room ($roomId) command string", error.toException())
                close(error.toException())
            }
        }

        commandNodeRef.addValueEventListener(listener)
        Log.d(TAG, "Attached command string listener for room: $roomId")

        awaitClose {
            Log.d(TAG, "Detaching command string listener for room: $roomId")
            commandNodeRef.removeEventListener(listener)
        }
    }

    override suspend fun sendRoomCommandString(
        roomId: String,
        commandString: String
    ): Result<Unit> {
        if (roomId.isBlank()) {
            Log.w(TAG, "Room ID is blank for sending command string.")
            return Result.failure(IllegalArgumentException("Room ID cannot be blank"))
        }
        if (commandString.isBlank()) {
            Log.w(TAG, "Command string is blank for room: $roomId")
        }

        return try {
            roomsRef.child(roomId).child("controller_command").setValue(commandString).await()
            Log.d(TAG, "Room ($roomId) command string sent: $commandString")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Error sending room ($roomId) command string: $commandString", e)
            Result.failure(e)
        }
    }
}
