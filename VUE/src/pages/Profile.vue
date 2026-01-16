<template>
  <div class="profile-container">
    <div class="profile-box">
      <h1>My Profile</h1>
      <form @submit.prevent="handleSubmit">
        <!-- Undergraduate Tier -->
        <div class="form-group">
          <label for="undergradTier">Undergraduate Tier</label>
          <select
            id="undergradTier"
            v-model="form.undergradTier"
            @blur="validateUndergradTier"
          >
            <option value="">Select your undergraduate tier</option>
            <option value="985">985</option>
            <option value="211">211</option>
            <option value="DOUBLE_NON">Double-Non</option>
            <option value="OTHER">Other</option>
          </select>
          <span v-if="errors.undergradTier" class="error">{{ errors.undergradTier }}</span>
        </div>

        <!-- GPA -->
        <div class="form-group">
          <label for="gpa">GPA</label>
          <input
            id="gpa"
            v-model.number="form.gpa"
            type="number"
            step="0.01"
            min="0"
            max="5"
            placeholder="Enter your GPA (0-5.0)"
            @blur="validateGpa"
          >
          <span v-if="errors.gpa" class="error">{{ errors.gpa }}</span>
        </div>

        <!-- GPA Scale -->
        <div class="form-group">
          <label for="gpaScale">GPA Scale</label>
          <input
            id="gpaScale"
            v-model="form.gpaScale"
            type="text"
            placeholder="e.g., 4.0, 5.0"
            @blur="validateGpaScale"
          >
          <span v-if="errors.gpaScale" class="error">{{ errors.gpaScale }}</span>
        </div>

        <!-- CET-4 Score -->
        <div class="form-group">
          <label for="cet4Score">CET-4 Score</label>
          <input
            id="cet4Score"
            v-model.number="form.cet4Score"
            type="number"
            min="0"
            max="710"
            placeholder="Enter your CET-4 score (0-710)"
            @blur="validateCet4Score"
          >
          <span v-if="errors.cet4Score" class="error">{{ errors.cet4Score }}</span>
        </div>

        <!-- CET-6 Score -->
        <div class="form-group">
          <label for="cet6Score">CET-6 Score</label>
          <input
            id="cet6Score"
            v-model.number="form.cet6Score"
            type="number"
            min="0"
            max="710"
            placeholder="Enter your CET-6 score (0-710)"
            @blur="validateCet6Score"
          >
          <span v-if="errors.cet6Score" class="error">{{ errors.cet6Score }}</span>
        </div>

        <!-- Target Score -->
        <div class="form-group">
          <label for="targetScore">Target Score</label>
          <input
            id="targetScore"
            v-model.number="form.targetScore"
            type="number"
            min="0"
            max="500"
            placeholder="Enter your target score (0-500)"
            @blur="validateTargetScore"
          >
          <span v-if="errors.targetScore" class="error">{{ errors.targetScore }}</span>
        </div>

        <!-- Other Scores -->
        <div class="form-group">
          <label for="otherScores">Other Scores (Optional)</label>
          <textarea
            id="otherScores"
            v-model="form.otherScores"
            placeholder="Enter any other relevant scores or information"
            rows="4"
          ></textarea>
          <span v-if="errors.otherScores" class="error">{{ errors.otherScores }}</span>
        </div>

        <!-- Submit Button -->
        <button type="submit" class="btn-submit" :disabled="loading">
          {{ loading ? 'Saving...' : 'Save Profile' }}
        </button>

        <!-- Messages -->
        <div v-if="message.error" class="message error-message">
          {{ message.error }}
        </div>
        <div v-if="message.success" class="message success-message">
          {{ message.success }}
        </div>
      </form>
    </div>
  </div>
</template>

<script>
import userService from '../services/userService'

export default {
  name: 'Profile',
  data() {
    return {
      form: {
        undergradTier: '',
        gpa: null,
        gpaScale: '',
        cet4Score: null,
        cet6Score: null,
        targetScore: null,
        otherScores: ''
      },
      errors: {
        undergradTier: '',
        gpa: '',
        gpaScale: '',
        cet4Score: '',
        cet6Score: '',
        targetScore: '',
        otherScores: ''
      },
      message: {
        error: '',
        success: ''
      },
      loading: false
    }
  },
  mounted() {
    this.loadProfile()
  },
  methods: {
    async loadProfile() {
      try {
        const response = await userService.getUserProfile()
        const profile = response.data.data
        
        this.form = {
          undergradTier: profile.undergradTier || '',
          gpa: profile.gpa ? parseFloat(profile.gpa) : null,
          gpaScale: profile.gpaScale || '',
          cet4Score: profile.cet4Score || null,
          cet6Score: profile.cet6Score || null,
          targetScore: profile.targetScore || null,
          otherScores: profile.otherScores || ''
        }
      } catch (error) {
        this.message.error = error.response?.data?.message || 'Failed to load profile'
      }
    },
    validateUndergradTier() {
      if (this.form.undergradTier && !['985', '211', 'DOUBLE_NON', 'OTHER'].includes(this.form.undergradTier)) {
        this.errors.undergradTier = 'Invalid undergraduate tier'
      } else {
        this.errors.undergradTier = ''
      }
    },
    validateGpa() {
      if (this.form.gpa !== null && this.form.gpa !== '') {
        if (this.form.gpa < 0 || this.form.gpa > 5.0) {
          this.errors.gpa = 'GPA must be between 0 and 5.0'
        } else {
          this.errors.gpa = ''
        }
      } else {
        this.errors.gpa = ''
      }
    },
    validateGpaScale() {
      if (this.form.gpaScale && this.form.gpaScale.trim().length === 0) {
        this.errors.gpaScale = ''
      } else {
        this.errors.gpaScale = ''
      }
    },
    validateCet4Score() {
      if (this.form.cet4Score !== null && this.form.cet4Score !== '') {
        if (this.form.cet4Score < 0 || this.form.cet4Score > 710) {
          this.errors.cet4Score = 'CET-4 score must be between 0 and 710'
        } else {
          this.errors.cet4Score = ''
        }
      } else {
        this.errors.cet4Score = ''
      }
    },
    validateCet6Score() {
      if (this.form.cet6Score !== null && this.form.cet6Score !== '') {
        if (this.form.cet6Score < 0 || this.form.cet6Score > 710) {
          this.errors.cet6Score = 'CET-6 score must be between 0 and 710'
        } else {
          this.errors.cet6Score = ''
        }
      } else {
        this.errors.cet6Score = ''
      }
    },
    validateTargetScore() {
      if (this.form.targetScore !== null && this.form.targetScore !== '') {
        if (this.form.targetScore < 0 || this.form.targetScore > 500) {
          this.errors.targetScore = 'Target score must be between 0 and 500'
        } else {
          this.errors.targetScore = ''
        }
      } else {
        this.errors.targetScore = ''
      }
    },
    validateOtherScores() {
      this.errors.otherScores = ''
    },
    async handleSubmit() {
      // Validate all fields
      this.validateUndergradTier()
      this.validateGpa()
      this.validateGpaScale()
      this.validateCet4Score()
      this.validateCet6Score()
      this.validateTargetScore()
      this.validateOtherScores()

      // Check if there are any errors
      const hasErrors = Object.values(this.errors).some(error => error !== '')
      if (hasErrors) {
        return
      }

      this.loading = true
      this.message.error = ''
      this.message.success = ''

      try {
        // Prepare data for submission
        const profileData = {
          undergradTier: this.form.undergradTier || null,
          gpa: this.form.gpa !== null ? this.form.gpa : null,
          gpaScale: this.form.gpaScale || null,
          cet4Score: this.form.cet4Score !== null ? this.form.cet4Score : null,
          cet6Score: this.form.cet6Score !== null ? this.form.cet6Score : null,
          targetScore: this.form.targetScore !== null ? this.form.targetScore : null,
          otherScores: this.form.otherScores || null
        }

        const response = await userService.updateUserProfile(profileData)
        this.message.success = 'Profile updated successfully!'
        
        // Update form with response data
        const updatedProfile = response.data.data
        this.form = {
          undergradTier: updatedProfile.undergradTier || '',
          gpa: updatedProfile.gpa ? parseFloat(updatedProfile.gpa) : null,
          gpaScale: updatedProfile.gpaScale || '',
          cet4Score: updatedProfile.cet4Score || null,
          cet6Score: updatedProfile.cet6Score || null,
          targetScore: updatedProfile.targetScore || null,
          otherScores: updatedProfile.otherScores || ''
        }
      } catch (error) {
        this.message.error = error.response?.data?.message || 'Failed to update profile. Please try again.'
      } finally {
        this.loading = false
      }
    }
  }
}
</script>

<style scoped>
.profile-container {
  display: flex;
  justify-content: center;
  align-items: flex-start;
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 40px 20px;
}

.profile-box {
  background: white;
  padding: 40px;
  border-radius: 8px;
  box-shadow: 0 10px 25px rgba(0, 0, 0, 0.2);
  width: 100%;
  max-width: 500px;
}

h1 {
  text-align: center;
  color: #333;
  margin-bottom: 30px;
  font-size: 28px;
}

.form-group {
  margin-bottom: 20px;
}

label {
  display: block;
  margin-bottom: 8px;
  color: #555;
  font-weight: 500;
}

input,
select,
textarea {
  width: 100%;
  padding: 12px;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 14px;
  transition: border-color 0.3s;
  font-family: inherit;
}

input:focus,
select:focus,
textarea:focus {
  outline: none;
  border-color: #667eea;
  box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
}

textarea {
  resize: vertical;
}

.error {
  display: block;
  color: #e74c3c;
  font-size: 12px;
  margin-top: 5px;
}

.btn-submit {
  width: 100%;
  padding: 12px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border: none;
  border-radius: 4px;
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
  transition: transform 0.2s, box-shadow 0.2s;
  margin-top: 10px;
}

.btn-submit:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 5px 15px rgba(102, 126, 234, 0.4);
}

.btn-submit:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.message {
  margin-top: 15px;
  padding: 12px;
  border-radius: 4px;
  font-size: 14px;
  text-align: center;
}

.error-message {
  background-color: #fadbd8;
  color: #c0392b;
  border: 1px solid #e74c3c;
}

.success-message {
  background-color: #d5f4e6;
  color: #27ae60;
  border: 1px solid #2ecc71;
}
</style>
