import NewItemComponent from "./ItemContainer/NewItemComponent.tsx";
import EditItemComponent from "./ItemContainer/EditItemComponent.tsx";
import {useTranslation} from "react-i18next";
import ItemIdContainer from "../../../../type/ItemIdContainer.tsx";
import RegistryConfig from "../../../../type/RegistryConfig.tsx";

export default function ItemContainer(
    {
        config,
        itemIdList,
        setItemIdList
    }: {
        readonly config: RegistryConfig,
        readonly itemIdList: ItemIdContainer[],
        readonly setItemIdList: (itemIdList: ItemIdContainer[]) => void
    }
) {
    const {t} = useTranslation();
    const addItemId = function (itemId: ItemIdContainer) {
        setItemIdList([...itemIdList, itemId]);
    }
    const removeItemId = function (itemIdToRemove: ItemIdContainer) {
        setItemIdList(
            itemIdList.filter(
                (itemId) => itemIdToRemove !== itemId)
        );
    }

    return <>
        <h2>Item Container</h2>
        <NewItemComponent config={config} addItemId={addItemId}/>
        {
            (itemIdList.length > 0) ?
                itemIdList.map(
                    (itemId) => <EditItemComponent config={config} key={itemId.privateId} itemId={itemId} removeItemId={removeItemId}/>
                ) : <div>{t("add_some_products")}</div>
        }
    </>
}
