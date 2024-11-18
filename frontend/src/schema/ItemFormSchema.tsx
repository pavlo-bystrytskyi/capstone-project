import * as yup from "yup";
import ItemStatus from "../type/ItemStatus.tsx";

const itemFormSchema = yup.object().shape({
    product: yup.object().shape({
        link: yup
            .string()
            .url("item_link_invalid")
            .required("item_link_required"),
        title: yup
            .string()
            .required("item_name_required")
            .min(4, "item_name_too_short")
            .max(255, "item_name_too_long"),
        description: yup
            .string()
            .max(4095, "item_description_too_long")
            .default(""),
    }),
    quantity: yup
        .number()
        .required("item_quantity_required")
        .positive("item_quantity_positive")
        .integer("item_quantity_integer")
        .default(1),
    status: yup
        .mixed<ItemStatus>()
        .oneOf(Object.values(ItemStatus), "item_status_invalid")
        .required("item_status_required")
        .default(ItemStatus.AVAILABLE),
});

export default itemFormSchema;
