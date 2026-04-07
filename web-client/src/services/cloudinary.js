const CLOUDINARY_UPLOAD_PRESET = 'hospital_profiles' // Change to your upload preset
const CLOUDINARY_CLOUD_NAME = 'ddzfxi8mc' // Change to your cloud name

/**
 * Uploads an image file to Cloudinary.
 * 
 * @param {File} file the image file to upload
 * @returns {Promise<string>} the secure URL of the uploaded image
 */
export const uploadImageToCloudinary = async (file) => {
  if (!file) return null

  const formData = new FormData()
  formData.append('file', file)
  formData.append('upload_preset', CLOUDINARY_UPLOAD_PRESET)

  const response = await fetch(`https://api.cloudinary.com/v1_1/${CLOUDINARY_CLOUD_NAME}/image/upload`, {
    method: 'POST',
    body: formData,
  })

  if (!response.ok) {
    const errorBody = await response.json().catch(() => ({}))
    throw new Error(errorBody.error?.message || 'Failed to upload image to Cloudinary.')
  }

  const data = await response.json()
  return data.secure_url
}
