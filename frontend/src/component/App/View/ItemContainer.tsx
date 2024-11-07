import ItemComponent from "./ItemContainer/ItemComponent.tsx";
import ViewConfig from "../../../type/ViewConfig.tsx";

export default function ItemContainer(
    {
        itemIdList,
        config
    }: {
        itemIdList: string[],
        config: ViewConfig
    }
) {
    return <>
        <h2>Item Container</h2>
        {
            itemIdList.map(
                (itemId) => <ItemComponent key={itemId} itemId={itemId} config={config}/>
            )
        }
    </>
}
