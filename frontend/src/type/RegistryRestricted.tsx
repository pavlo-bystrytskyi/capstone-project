import Registry from "./Registry.tsx";

type RegistryRestricted = Pick<Registry, "title" | "description" | "itemIds">;

export default RegistryRestricted
