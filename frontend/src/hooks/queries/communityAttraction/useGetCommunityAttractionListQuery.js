import { useQuery } from '@tanstack/vue-query'
import * as communityAttractionAPI from '../../../lib/api/communityAttractionAPI'

export const useGetCommunityAttractionListQuery = (communityId) => {
  const { isPending, data, refetch } = useQuery({
    queryKey: ['communityAttractionList', communityId],
    queryFn: () => communityAttractionAPI.getCommunityAttractionList({ communityid : communityId }),
    enabled: !!communityId,
    select: (res) => res.data,
  })

  return { isPending, data, refetch }
}
