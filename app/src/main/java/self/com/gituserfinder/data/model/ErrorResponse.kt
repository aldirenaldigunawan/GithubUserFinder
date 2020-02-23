package self.com.gituserfinder.data.model

import com.google.gson.annotations.SerializedName

data class ErrorResponse(@SerializedName("message") override val message: String) : Exception(message)