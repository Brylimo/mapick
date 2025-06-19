import { useQuery } from '@tanstack/vue-query'
import * as communityAPI from '../../../lib/api/communityAPI'

export const useGetCommunityListQuery = () => {
    const { isPending, data } = useQuery({
        queryKey: ['communityList'],
        queryFn: communityAPI.getCommunityList,
        select: (res) => res.data
    })
        
    return { isPending, data }
}