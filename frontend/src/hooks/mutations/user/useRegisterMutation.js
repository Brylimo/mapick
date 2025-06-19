import { useMutation } from '@tanstack/vue-query'
import * as userAPI from '../../../lib/api/userAPI'

export const useRegisterMutation = () => {
  return useMutation({
    mutationFn: userAPI.register,
  })
}
