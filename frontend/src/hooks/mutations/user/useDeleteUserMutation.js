import { useMutation } from "@tanstack/vue-query";
import * as userAPI from '../../../lib/api/userAPI'

export const useDeleteUserMutation = () => {
    return useMutation({
        mutationFn: userAPI.deleteUser
    })
}