import {useEffect} from "react";
import axios from "axios";
import {useTranslation} from "react-i18next";
import {SubmitHandler, useForm} from "react-hook-form";
import {yupResolver} from "@hookform/resolvers/yup";
import ItemIdContainer from "../../../../../type/ItemIdContainer.tsx";
import ItemStatus from "../../../../../type/ItemStatus.tsx";
import RegistryConfig from "../../../../../type/RegistryConfig.tsx";
import {emptyItem} from "../../../../../type/EmptyItem.tsx";
import ItemRestricted from "../../../../../type/ItemRestricted.tsx";
import itemFormSchema from "../../../../../schema/ItemFormSchema.tsx";

export default function EditItemComponent({
                                              config,
                                              itemId,
                                              removeItemId,
                                          }: {
    readonly config: RegistryConfig;
    readonly itemId: ItemIdContainer;
    readonly removeItemId: (itemId: ItemIdContainer) => void;
}) {
    const {t} = useTranslation();
    const {
        register,
        handleSubmit,
        setValue,
        formState: {errors},
    } = useForm<ItemRestricted>({
        resolver: yupResolver(itemFormSchema),
        defaultValues: emptyItem,
    });
    useEffect(() => {
        axios
            .get<ItemRestricted>(`${config.item.url}/${itemId.privateId}`)
            .then((result) => {
                const item = result.data;
                Object.entries(item).forEach(([key, value]) => {
                    setValue(key as keyof ItemRestricted, value);
                });
            })
            .catch((error) => {
                console.error("Error fetching data:", error);
            });
    }, [itemId, setValue, config]);
    const removeItem = () => {
        axios
            .delete(`${config.item.url}/${itemId.privateId}`)
            .then(() => {
                removeItemId(itemId);
            })
            .catch((error) => {
                console.error("Error deleting item:", error);
            });
    };
    const saveItem: SubmitHandler<ItemRestricted> = (data) => {
        axios
            .put(`${config.item.url}/${itemId.privateId}`, data)
            .then(() => {
                console.log("ItemRestricted saved successfully");
            })
            .catch((error) => {
                console.error("Error saving item:", error);
            });
    };

    return (
        <form className="item-form" onSubmit={handleSubmit(saveItem)}>
            <label htmlFor="title">{t("item_name")}</label>
            <input
                type="text"
                {...register("product.title")}
                className={errors.product?.title ? "error" : ""}
            />
            {errors.product?.title?.message && <p>{t(errors.product.title.message)}</p>}
            <label htmlFor="description">{t("item_description")}</label>
            <input
                type="text"
                {...register("product.description")}
                className={errors.product?.description ? "error" : ""}
            />
            {errors.product?.description?.message && <p>{t(errors.product.description.message)}</p>}
            <label htmlFor="link">{t("item_link")}</label>
            <input
                type="text"
                {...register("product.link")}
                className={errors.product?.link ? "error" : ""}
            />
            {errors.product?.link?.message && <p>{t(errors.product.link.message)}</p>}
            <label htmlFor="quantity">{t("item_quantity")}</label>
            <input
                type="number"
                {...register("quantity")}
                className={errors.quantity ? "error" : ""}
            />
            {errors.quantity?.message && <p>{t(errors.quantity.message)}</p>}
            <label htmlFor="status">{t("item_status")}</label>
            <select
                {...register("status")}
                className={errors.status ? "error" : ""}
            >
                <option value={ItemStatus.AVAILABLE}>{t("status_available")}</option>
                <option value={ItemStatus.RESERVED}>{t("status_reserved")}</option>
                <option value={ItemStatus.PURCHASED}>{t("status_purchased")}</option>
            </select>
            {errors.status?.message && <p>{t(errors.status.message)}</p>}
            <button type="submit">{t("item_save")}</button>
            <button type="button" onClick={removeItem}>
                {t("item_remove")}
            </button>
        </form>
    );
}
