import Registry from "../../../type/Registry.tsx";

export default function ListElement({registry}: { registry: Registry }) {
    const host = window.location.origin
    const link = host + "/show-user/" + registry.privateId;

    return <a href={link}>{registry.title}</a>
}