import {SubmitHandler, useForm} from "react-hook-form";
import {yupResolver} from "@hookform/resolvers/yup";
import axios from "axios";
import {useTranslation} from "react-i18next";
import {emptyItem} from "../../../../../type/EmptyItem";
import ItemIdContainer from "../../../../../type/ItemIdContainer";
import RegistryConfig from "../../../../../type/RegistryConfig";
import ItemRestricted from "../../../../../type/ItemRestricted.tsx";
import itemFormSchema from "../../../../../schema/ItemFormSchema.tsx";

export default function NewItemComponent(
    {
        config,
        addItemId
    }: {
        readonly config: RegistryConfig,
        readonly addItemId: (itemId: ItemIdContainer) => void,
    }
) {
    const {t} = useTranslation();
    const {
        register,
        handleSubmit,
        reset,
        formState: {errors},
    } = useForm<ItemRestricted>({
        resolver: yupResolver(itemFormSchema),
        defaultValues: emptyItem,
    });
    const onSubmit: SubmitHandler<ItemRestricted> = (data) => {
        axios.post<ItemIdContainer>(config.item.url, data)
            .then(response => {
                addItemId({
                    publicId: response.data.publicId,
                    privateId: response.data.privateId
                });
                reset();
            })
            .catch(error => {
                console.error('Error fetching data:', error);
            });
    };

    return (
        <form className="new-item-form" onSubmit={handleSubmit(onSubmit)}>
            <div>
                <label htmlFor="title">{t("item_name")}</label>
                <input type="text" {...register("product.title")} />
                {errors.product?.title?.message && (
                    <p className="validation-error">{t(errors.product.title.message)}</p>
                )}
            </div>
            <div>
                <label htmlFor="description">{t("item_description")}</label>
                <input type="text" {...register("product.description")} />
                {errors.product?.description?.message && (
                    <p className="validation-error">{t(errors.product.description.message)}</p>
                )}
            </div>
            <div>
                <label htmlFor="link">{t("item_link")}</label>
                <input type="text" {...register("product.link")} />
                {errors.product?.link?.message && (
                    <p className="validation-error">{t(errors.product.link.message)}</p>
                )}
            </div>
            <div>
                <label htmlFor="quantity">{t("item_quantity")}</label>
                <input type="number" {...register("quantity")} />
                {errors.quantity?.message && (
                    <p className="validation-error">{t(errors.quantity.message)}</p>
                )}
            </div>
            <button type="submit">{t("item_add")}</button>
        </form>
    );
}
