import {Button, Modal} from 'react-bootstrap';
import {useTranslation} from "react-i18next";

export default function LoginModal(
    {
        show, onClose
    }: {
        show: boolean;
        onClose: () => void;
    }
) {
    const {t} = useTranslation();

    return <Modal show={show} onHide={onClose} centered>
        <Modal.Header closeButton>
            <Modal.Title>{t("modal_restricted_title")}</Modal.Title>
        </Modal.Header>
        <Modal.Body>{t("modal_restricted_body")}</Modal.Body>
        <Modal.Footer>
            <Button variant="primary" onClick={onClose}>
                {t("modal_restricted_button")}
            </Button>
        </Modal.Footer>
    </Modal>
}
