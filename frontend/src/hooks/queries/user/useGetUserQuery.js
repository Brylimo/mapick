import { useQuery } from '@tanstack/vue-query'
import * as userAPI from '../../../lib/api/userAPI'

export const useGetUserQuery = () => {
  const { isPending, data } = useQuery({
    queryKey: ['getUser'],
    queryFn: userAPI.getUser
  })
    
    return { isPending, data }
}
