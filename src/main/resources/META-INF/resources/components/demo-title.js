import { LitElement, html, css } from 'lit';

export class DemoTitle extends LitElement {

  static styles = css`
      h1 {
        font-family: "Red Hat Mono", monospace;
        font-size: 60px;
        font-style: normal;
        font-variant: normal;
        font-weight: 700;
        line-height: 26.4px;
        color: var(--main-highlight-text-color);
      }

      .title {
        text-align: center;
        padding: 1em;
        background: var(--main-bg-color);
      }
      
      .explanation {
        margin-left: auto;
        margin-right: auto;
        width: 50%;
        text-align: justify;
        font-size: 20px;
      }
      
      .explanation img {
        max-width: 60%;
        display: block;
        float:left;
        margin-right: 2em;
        margin-top: 1em;
      }
    `

  render() {
    return html`
            <div class="title">
                <h1>C2 Chatbot</h1>
            </div>
            <div class="explanation">
                This demo shows how to build a chat bot designed to extract land unit data
                and their associated locations from natural language input and visualize them on a map
                using MIL-STD-2525D symbology. A summary of unit actions can be desplayed by click on symbol.
            </div>
        `
  }


}

customElements.define('demo-title', DemoTitle);