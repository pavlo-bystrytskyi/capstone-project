import NewItemForm from "./ItemContainer/NewItemForm.tsx";
import ItemComponent from "./ItemContainer/ItemComponent.tsx";
import {useTranslation} from "react-i18next";
import ItemId from "../../../../type/ItemId.tsx";

export default function ItemContainer(
    {
        itemIdList,
        setItemIdList
    }: {
        itemIdList: ItemId[],
        setItemIdList: (itemIdList: ItemId[]) => void
    }
) {
    const {t} = useTranslation();
    const addItemId = function (itemId: ItemId) {
        setItemIdList([...itemIdList, itemId]);
    }
    const removeItemId = function (itemIdToRemove: ItemId) {
        setItemIdList(
            itemIdList.filter(
                (itemId) => itemIdToRemove !== itemId)
        );
    }

    return <>
        <h2>Item Container</h2>
        <NewItemForm addItemId={addItemId}/>
        {
            (itemIdList.length > 0) ?
                itemIdList.map(
                    (itemId) => <ItemComponent key={itemId.privateId} itemId={itemId} removeItemId={removeItemId}/>
                ) : <div>{t("add_some_products")}</div>
        }
    </>
}
