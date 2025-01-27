import ItemIdContainer from "./ItemIdContainer.tsx";

type Registry = {
    privateId: string,
    publicId: string,
    title: string,
    description: string,
    active: boolean,
    deactivationDate: Date | null
    itemIds: ItemIdContainer[],
}

export default Registry