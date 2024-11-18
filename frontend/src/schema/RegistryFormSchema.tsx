import * as yup from "yup";

const registryFormSchema = yup.object({
    title: yup
        .string()
        .required("wishlist_title_required")
        .min(4, "wishlist_title_too_short")
        .max(255, "wishlist_title_too_long"),
    description: yup
        .string()
        .max(4095, "wishlist_description_too_long")
        .default(""),
    itemIds: yup.array().of(
        yup.object({
            publicId: yup
                .string()
                .required(),
            privateId: yup
                .string()
                .required(),
        })
    )
        .required("wishlist_item_count_zero")
        .min(1, "wishlist_item_count_zero")
}).required();

export default registryFormSchema;
