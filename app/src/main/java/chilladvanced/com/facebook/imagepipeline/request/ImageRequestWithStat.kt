package chilladvanced

import com.facebook.imagepipeline.request.ImageRequest
import com.facebook.imagepipeline.request.ImageRequestBuilder

class ImageRequestWithStat(build: ImageRequestBuilder) : ImageRequest(build) {
    init {
        // TODO
        Logger.register(super.getSourceUri()?.host ?: "", NETWORK_METHODS.FRESCO)

    }

}
