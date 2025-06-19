import { useMutation } from '@tanstack/vue-query'
import * as userAPI from '../../../lib/api/userAPI'

export const useUpdateUserMutation = () => {
  return useMutation({
    mutationFn: userAPI.updateUser,
  })
}
