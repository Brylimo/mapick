import { useMutation } from '@tanstack/vue-query'
import * as communityAPI from '../../../lib/api/communityAPI'

export const useCommunityCreateMutation = () => {
  return useMutation({
    mutationFn: communityAPI.createCommunity,
  })
}
